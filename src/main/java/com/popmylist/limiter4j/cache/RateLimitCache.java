package com.popmylist.limiter4j.cache;

import com.popmylist.limiter4j.KeyUtil;
import com.popmylist.limiter4j.model.RateLimitRequest;
import com.popmylist.limiter4j.model.RateLimitResponse;
import java.util.concurrent.TimeUnit;
import net.jodah.expiringmap.ExpiringMap;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class RateLimitCache {
  private final ExpiringMap<String, RateLimitInfo> cache;
  private final KeyUtil keyUtil;

  public RateLimitCache(KeyUtil keyUtil) {
    this.keyUtil = keyUtil;
    this.cache = ExpiringMap.builder()
        .variableExpiration()
        .build();
  }

  public Mono<RateLimitResponse> getRateLimitResponse(final RateLimitRequest request) {
    final String key = keyUtil.getHashKey(request.getUniqueKey());
    RateLimitResponse response = new RateLimitResponse();
    RateLimitInfo rateLimitInfo;
    if (cache.containsKey(key)) {
      rateLimitInfo = cache.get(key);
      long val = rateLimitInfo.reduceHits(request.getHits());
      if (val > 0) {
        response.setStatus(0);
      }
    } else {
      rateLimitInfo = new RateLimitInfo(request.getLimit(), request.getDuration());
      rateLimitInfo.reduceHits(request.getHits());
      cache.put(key, rateLimitInfo, request.getDuration(), TimeUnit.MILLISECONDS);
      response.setStatus(0);
    }
    response.setLimit(rateLimitInfo.getLimit());
    response.setDuration(rateLimitInfo.getDuration());
    response.setResetTime(rateLimitInfo.getResetTime());
    return Mono.just(response);
  }

}
