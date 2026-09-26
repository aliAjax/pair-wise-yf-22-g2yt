package com.generated.qualityTrace.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/** 轻量 HS256 JWT 签发/校验，密钥来自 JWT_SECRET */
public final class JwtUtils {
  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static final Base64.Encoder B64 = Base64.getUrlEncoder().withoutPadding();
  private static final Base64.Decoder B64D = Base64.getUrlDecoder();

  private JwtUtils() {}

  public static String issue(String subject, String role, String secret, long ttlSeconds) {
    try {
      long now = System.currentTimeMillis() / 1000;
      Map<String, Object> payload = new LinkedHashMap<>();
      payload.put("sub", subject);
      payload.put("role", role);
      payload.put("iat", now);
      payload.put("exp", now + ttlSeconds);
      String header = B64.encodeToString("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes(StandardCharsets.UTF_8));
      String body = B64.encodeToString(MAPPER.writeValueAsBytes(payload));
      String data = header + "." + body;
      return data + "." + B64.encodeToString(hmac(data, secret));
    } catch (Exception e) {
      throw new IllegalStateException("jwt issue failed", e);
    }
  }

  /** 校验签名与过期时间，返回 claims；非法 token 抛 SecurityException */
  @SuppressWarnings("unchecked")
  public static Map<String, Object> verify(String token, String secret) {
    try {
      String[] parts = token.split("\\.");
      if (parts.length != 3) {
        throw new SecurityException("malformed token");
      }
      String data = parts[0] + "." + parts[1];
      byte[] expected = hmac(data, secret);
      byte[] actual = B64D.decode(parts[2]);
      if (!MessageDigest.isEqual(expected, actual)) {
        throw new SecurityException("bad signature");
      }
      Map<String, Object> claims = MAPPER.readValue(B64D.decode(parts[1]), Map.class);
      Object exp = claims.get("exp");
      if (!(exp instanceof Number) || ((Number) exp).longValue() < System.currentTimeMillis() / 1000) {
        throw new SecurityException("token expired");
      }
      return claims;
    } catch (SecurityException e) {
      throw e;
    } catch (Exception e) {
      throw new SecurityException("invalid token", e);
    }
  }

  private static byte[] hmac(String data, String secret) throws Exception {
    Mac mac = Mac.getInstance("HmacSHA256");
    mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
    return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
  }
}
