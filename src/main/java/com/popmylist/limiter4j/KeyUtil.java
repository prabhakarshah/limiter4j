package com.popmylist.limiter4j;

import java.nio.charset.StandardCharsets;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

@Component
public class KeyUtil {

  public String getHashKey(String uniqueKey) {
    return DigestUtils.md5DigestAsHex(uniqueKey.getBytes(StandardCharsets.UTF_8));
  }

}
