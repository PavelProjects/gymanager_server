package ru.pobopo.services.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProxyConfig {
    @Bean
    RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("user_service_route",
                route -> route.path("/user/**")
                    .uri("http://localhost:9090"))
            .route("auth_service_route",
                route -> route.path("/auth/**")
                    .uri("http://localhost:9090"))
            .build();
    }
}
