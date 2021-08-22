package com.popmylist.limiter4j.handler;

import com.popmylist.limiter4j.model.RateLimitRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class RateLimitRequestHandler {
  public Mono<ServerResponse> getRateLimt(ServerRequest request) {
    Mono<RateLimitRequest> rateLimitRequest = request.bodyToMono(RateLimitRequest.class);
    return ServerResponse.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(rateLimitRequest, RateLimitRequest.class);
  }
}
