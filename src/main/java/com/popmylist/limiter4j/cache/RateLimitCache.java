package com.popmylist.limiter4j.cache;

import com.popmylist.limiter4j.KeyUtil;
import com.popmylist.limiter4j.model.RateLimitRequest;
import com.popmylist.limiter4j.model.RateLimitResponse;
import java.util.concurrent.TimeUnit;
import lombok.extern.log4j.Log4j2;
import net.jodah.expiringmap.ExpirationListener;
import net.jodah.expiringmap.ExpiringMap;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Log4j2
@Component
public class RateLimitCache {
  private final ExpiringMap<String, RateLimitInfo> cache;
  private final KeyUtil keyUtil;
  private final Scheduler scheduler;

  public RateLimitCache(KeyUtil keyUtil) {
    this.keyUtil = keyUtil;
    this.cache = ExpiringMap.builder()
        .variableExpiration()
        .asyncExpirationListener(new LimitExpirationListener())
        .build();
    scheduler = Schedulers.boundedElastic();
  }

  public Mono<RateLimitResponse> getRateLimitResponse(final Mono<RateLimitRequest> requestMono) {
    return requestMono
        .publishOn(scheduler)
        .map(request -> {
          final String key = keyUtil.getHashKey(request.getUniqueKey());
          RateLimitResponse response = new RateLimitResponse();
          RateLimitInfo rateLimitInfo;
          if (cache.containsKey(key)) {
            rateLimitInfo = cache.get(key);
            long val = rateLimitInfo.reduceHits(request.getHits());
            if (val > 0) {
              response.setStatus(0);
              response.setLimit(val);
            } else {
              response.setStatus(1);
              response.setLimit(0);
            }

          } else {
            rateLimitInfo = new RateLimitInfo(request.getLimit(), request.getDuration(), request.getUniqueKey());
            var val = rateLimitInfo.reduceHits(request.getHits());
            cache.put(key, rateLimitInfo, request.getDuration(), TimeUnit.MILLISECONDS);
            response.setStatus(0);
            response.setLimit(val);
          }
          response.setDuration(rateLimitInfo.getDuration());
          response.setResetTime(rateLimitInfo.getResetTime());
          return response;
        });
  }

  public static class LimitExpirationListener implements ExpirationListener<String, RateLimitInfo> {

    @Override public void expired(String s, RateLimitInfo rateLimitInfo) {
      log.info("Key Removed {}:{} at {}", s, rateLimitInfo.getUniqueKey(), rateLimitInfo.getResetTime());
    }
  }
}
