package ru.pobopo.gymanager.services.gateway.config;

import java.net.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class ProxyConfig {
    private final String userServicePath;
    private final String authServicePath;

    @Autowired
    public ProxyConfig(Environment env) {
        this.userServicePath = env.getProperty("USER_SERVICE_PATH", "http://192.168.1.230:9090");
        this.authServicePath = env.getProperty("AUTH_SERVICE_PATH", "http://192.168.1.230:9090");
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("user_service_route",
                route -> route.path("/user/**")
                    .uri(userServicePath))
            .route("auth_service_route",
                route -> route.path("/auth/**")
                    .uri(authServicePath))
            .build();
    }
}
