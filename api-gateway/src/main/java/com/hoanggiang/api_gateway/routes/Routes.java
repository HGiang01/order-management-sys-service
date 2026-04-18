package com.hoanggiang.api_gateway.routes;

import org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

import java.net.URI;

import static org.springframework.cloud.gateway.server.mvc.filter.FilterFunctions.setPath;

@Configuration
public class Routes {
    @Bean
    public RouterFunction<ServerResponse> productServiceRoute() {
        return GatewayRouterFunctions.route("product-service")
                                     .route(RequestPredicates.path("/api/v1/product"), HandlerFunctions.http())
                                     .before(BeforeFilterFunctions.uri("http://localhost:8080"))
                                     .filter(CircuitBreakerFilterFunctions.circuitBreaker("productServiceCircuitBreaker", URI.create("forward:/fallback")))
                                     .build();
    }

    @Bean
    public RouterFunction<ServerResponse> productServiceSwaggerRoute() {
        return GatewayRouterFunctions.route("product-service-swagger")
                                     .route(RequestPredicates.path("/aggregate/product-server/v3/api-docs"), HandlerFunctions.http())
                                     .before(BeforeFilterFunctions.uri("http://localhost:8080"))
                                     .filter(CircuitBreakerFilterFunctions.circuitBreaker("productServiceSwaggerCircuitBreaker", URI.create("forward:/fallback")))
                                     .filter(setPath("/api-docs"))
                                     .build();
    }

    @Bean
    public RouterFunction<ServerResponse> orderServiceRoute() {
        return GatewayRouterFunctions.route("order-service")
                                     .route(RequestPredicates.path("/api/v1/order"), HandlerFunctions.http())
                                     .before(BeforeFilterFunctions.uri("http://localhost:8081"))
                                     .filter(CircuitBreakerFilterFunctions.circuitBreaker("orderServiceCircuitBreaker", URI.create("forward:/fallback")))
                                     .build();
    }

    @Bean
    public RouterFunction<ServerResponse> orderServiceSwaggerRoute() {
        return GatewayRouterFunctions.route("order-service-swagger")
                                     .route(RequestPredicates.path("/aggregate/order-server/v3/api-docs"), HandlerFunctions.http())
                                     .before(BeforeFilterFunctions.uri("http://localhost:8081"))
                                     .filter(CircuitBreakerFilterFunctions.circuitBreaker("orderServiceSwaggerCircuitBreaker", URI.create("forward:/fallback")))
                                     .filter(setPath("/api-docs"))
                                     .build();
    }

    @Bean
    public RouterFunction<ServerResponse> inventoryServiceRoute() {
        return GatewayRouterFunctions.route("inventory-service")
                                     .route(RequestPredicates.path("/api/v1/inventory"), HandlerFunctions.http())
                                     .before(BeforeFilterFunctions.uri("http://localhost:8082"))
                                     .filter(CircuitBreakerFilterFunctions.circuitBreaker("inventoryServiceCircuitBreaker", URI.create("forward:/fallback")))
                                     .build();
    }

    @Bean
    public RouterFunction<ServerResponse> inventoryServiceSwaggerRoute() {
        return GatewayRouterFunctions.route("inventory-service-swagger")
                                     .route(RequestPredicates.path("/aggregate/inventory-server/v3/api-docs"), HandlerFunctions.http())
                                     .before(BeforeFilterFunctions.uri("http://localhost:8082"))
                                     .filter(CircuitBreakerFilterFunctions.circuitBreaker("inventoryServiceSwaggerCircuitBreaker", URI.create("forward:/fallback")))
                                     .filter(setPath("/api-docs"))
                                     .build();
    }

    // Resilience4j
    @Bean
    public RouterFunction<ServerResponse> fallbackRoute() {
        return RouterFunctions.route()
                              .GET("/fallback", request -> ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                                                                         .body("Service Unavailable, please try again later"))
                              .build();

    }
}
