package ru.pobopo.gymanager.services.gateway.filter;

import static ru.pobopo.gymanager.shared.constants.HeadersNames.CURRENT_REQUEST_ID;
import static ru.pobopo.gymanager.shared.constants.HeadersNames.USER_ID_HEADER;
import static ru.pobopo.gymanager.shared.constants.HeadersNames.USER_LOGIN_HEADER;
import static ru.pobopo.gymanager.shared.constants.HeadersNames.USER_ROLES_HEADER;

import com.google.gson.Gson;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.pobopo.gymanager.services.gateway.exception.AccessDeniedException;
import ru.pobopo.gymanager.services.gateway.exception.MissingTokenException;
import ru.pobopo.gymanager.services.gateway.service.AuthenticationService;
import ru.pobopo.gymanager.shared.objects.AuthorizedUserInfo;
import ru.pobopo.gymanager.shared.objects.ErrorResponse;
import ru.pobopo.gymanager.shared.objects.UnprotectedPathsValidator;

@Slf4j
@Component
public class SecurityFilter implements GlobalFilter {
    private static final String TOKEN_HEADER = "Gymanager-Token";

    private final AuthenticationService authenticationService;
    private final UnprotectedPathsValidator unprotectedPathsValidator;
    private final Gson gson;

    @Autowired
    public SecurityFilter(
        AuthenticationService authenticationService,
        UnprotectedPathsValidator unprotectedPathsValidator,
        Gson gson
    ) {
        this.authenticationService = authenticationService;
        this.unprotectedPathsValidator = unprotectedPathsValidator;
        this.gson = gson;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest originalRequest = exchange.getRequest();
        try {
            List<String> token = originalRequest.getHeaders().get(TOKEN_HEADER);
            String currentRequestId = UUID.randomUUID().toString();
            ServerHttpRequest.Builder requestBuilder = originalRequest.mutate()
                .header(CURRENT_REQUEST_ID, currentRequestId);

            if (token == null || token.size() != 1) {
                if (!unprotectedPathsValidator.isUnprotectedPath(originalRequest.getPath().value())) {
                    throw new MissingTokenException("Token is missing!");
                }
                ServerWebExchange newExchange = exchange.mutate().request(requestBuilder.build()).build();
                return chain.filter(newExchange);
            }

            AuthorizedUserInfo userDetails = authenticationService.validateToken(currentRequestId, token.get(0));
            if (userDetails == null) {
                throw new AccessDeniedException("Bad token");
            }
            ServerHttpRequest request = requestBuilder
                .header(USER_LOGIN_HEADER, userDetails.getUserLogin())
                .header(USER_ID_HEADER, userDetails.getUserId())
                .header(USER_ROLES_HEADER, userDetails.getRoles())
                .build();
            ServerWebExchange newExchange = exchange.mutate().request(request).build();
            return chain.filter(newExchange);
        } catch (Exception exception) {
            log.warn(exception.getMessage(), exception);
            return processException(exception, exchange.getResponse());
        }
    }

    private Mono<Void> processException(Exception exception, ServerHttpResponse response) {
        ErrorResponse errorResponse = new ErrorResponse();
        if (exception != null) {
            errorResponse.setMessage(exception.getMessage());
            if (exception instanceof AccessDeniedException) {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
            } else if (exception instanceof MissingTokenException) {
                response.setStatusCode(HttpStatus.BAD_REQUEST);
            } else {
                response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        String responseJson = gson.toJson(errorResponse);
        DataBuffer buffer = response.bufferFactory().wrap(responseJson.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }
}
