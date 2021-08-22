package com.popmylist.limiter4j.model;

import lombok.Data;

@Data
public class RateLimitResponse {
  private int status;
  private long limit;
  private long duration;
  private long resetTime;
}
