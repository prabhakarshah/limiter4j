package com.popmylist.limiter4j.route;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

import com.popmylist.limiter4j.handler.RateLimitRequestHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration(proxyBeanMethods = false)
public class Router {
  @Bean
  public RouterFunction<ServerResponse> routerSetup(RateLimitRequestHandler handler) {
    return RouterFunctions.route(
        POST("/GetRateLimits").and(accept(MediaType.APPLICATION_JSON)), handler::getRateLimt
    );
  }
}
