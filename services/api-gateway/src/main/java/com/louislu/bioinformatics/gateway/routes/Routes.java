package com.louislu.bioinformatics.gateway.routes;

import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

@Configuration
public class Routes {

    @Bean
    public RouterFunction<ServerResponse> dataServiceRoutes() {
        return GatewayRouterFunctions.route("data_service")
                .route(RequestPredicates.path("/api/data/**"), HandlerFunctions.http("http://localhost:8082"))
                .build();
    }
}
