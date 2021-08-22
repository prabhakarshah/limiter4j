package com.popmylist.limiter4j.model;

import lombok.Data;

@Data
public class RateLimitRequest {
  private String name;
  private String uniqueKey;
  private int hits;
  private int duration;
  private long limit;
}
