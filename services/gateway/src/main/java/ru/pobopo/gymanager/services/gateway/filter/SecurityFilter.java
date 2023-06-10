package ru.pobopo.gymanager.services.gateway.filter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.nio.charset.StandardCharsets;
import java.util.List;
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
import ru.pobopo.gymanager.services.gateway.exception.MissingTokenException;
import ru.pobopo.gymanager.services.gateway.exception.UnauthorizedException;
import ru.pobopo.gymanager.services.gateway.service.AuthenticationService;
import ru.pobopo.gymanager.shared.objects.ErrorResponse;
import ru.pobopo.gymanager.shared.objects.UserDetailsResponse;
import org.apache.commons.lang3.StringUtils;

@Slf4j
@Component
public class SecurityFilter implements GlobalFilter {
    private static final String TOKEN_HEADER = "Gymanager-Token";
    private final static String USER_LOGIN_HEADER = "Current-User-Login";
    private final static String USER_ID_HEADER = "Current-User-Id";
    private final static String USER_ROLES_HEADER = "Current-User-Roles";

    private final AuthenticationService authenticationService;
    private final Gson gson;

    @Autowired
    public SecurityFilter(AuthenticationService authenticationService, Gson gson) {
        this.authenticationService = authenticationService;
        this.gson = gson;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest originalRequest = exchange.getRequest();
        try {
            List<String> token = originalRequest.getHeaders().get(TOKEN_HEADER);
            if (token == null || token.size() != 1) {
                if (!permitAllPath(originalRequest.getPath().value())) {
                    throw new MissingTokenException("Token is missing!");
                }
                return chain.filter(exchange);
            }

            UserDetailsResponse userDetails = authenticationService.validateToken(token.get(0));
            ServerHttpRequest request = originalRequest.mutate()
                .header(USER_LOGIN_HEADER, userDetails.getUserLogin())
                .header(USER_ID_HEADER, userDetails.getUserId())
                .header(USER_ROLES_HEADER, userDetails.getRoles())
                .build();
            ServerWebExchange exchange1 = exchange.mutate().request(request).build();

            return chain.filter(exchange1);
        } catch (Exception exception) {
            return processException(exception, exchange.getResponse());
        }
    }

    private Mono<Void> processException(Exception exception, ServerHttpResponse response) {
        ErrorResponse errorResponse = new ErrorResponse();
        if (exception != null) {
            errorResponse.setMessage(exception.getMessage());
            if (exception instanceof UnauthorizedException) {
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

    // Todo вынести список доступных путей без авторизации в переменные окружения
    private boolean permitAllPath(String path) {
        return StringUtils.equals(path, "/auth") || StringUtils.equals(path, "/user/create");
    }
}
