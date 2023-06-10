package ru.pobopo.services.gateway.filter;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.pobopo.services.gateway.objects.UserInfo;
import ru.pobopo.services.gateway.service.AuthenticationService;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;

@Slf4j
@Component
public class SecurityFilter implements GlobalFilter {
    private static final String TOKEN_HEADER = "Gymanager-Token";
    private final static String USER_LOGIN_HEADER = "Current-User-Login";
    private final static String USER_ID_HEADER = "Current-User-Id";
    private final static String USER_ROLES_HEADER = "Current-User-Roles";

    @Autowired
    private AuthenticationService authenticationService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        List<String> token = exchange.getRequest().getHeaders().get(TOKEN_HEADER);
        if (token == null || token.size() != 1) {
            log.warn("Token missing");
            return chain.filter(exchange);
        }

        UserInfo userDetails = authenticationService.validateToken(token.get(0));
        ServerHttpRequest request = exchange.getRequest()
            .mutate()
            .header(USER_LOGIN_HEADER, userDetails.getUserLogin())
            .header(USER_ID_HEADER, userDetails.getUserId())
            .header(USER_ROLES_HEADER, String.join(";", userDetails.getRoles()))
            .build();
        ServerWebExchange exchange1 = exchange.mutate().request(request).build();
        return chain.filter(exchange1);
    }
}
