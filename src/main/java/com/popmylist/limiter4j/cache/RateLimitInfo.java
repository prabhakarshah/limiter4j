package com.popmylist.limiter4j.cache;

import java.util.concurrent.atomic.AtomicLong;
import lombok.Getter;

public class RateLimitInfo {
  @Getter private String uniqueKey;
  private AtomicLong counter;
  @Getter private long duration;
  @Getter private long limit;
  @Getter private long resetTime;

  public RateLimitInfo(long limit, int duration, String uniqueKey) {
    this.limit = limit;
    counter = new AtomicLong(limit);
    this.duration = duration;
    this.resetTime = System.currentTimeMillis() + duration;
  }

  public long reduceHits(int hit) {
    return counter.addAndGet(-hit);
  }
}
