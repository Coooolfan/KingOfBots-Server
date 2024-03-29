package com.yang.kingofbotsserver.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {
    public static final long JWT_TTL = 60 * 60 * 1000L * 24 * 14;  // 有效期14天
    public static final String JWT_KEY = "SDFGjhdsfals76fhgKYJF685996fg5234232131afasdfac";

    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String createJWT(String subject) {
        JwtBuilder builder = getJwtBuilder(subject, getUUID());
        return builder.compact();
    }

    private static JwtBuilder getJwtBuilder(String subject, String uuid) {
        SecretKey secretKey = generalKey();
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        long expMillis = nowMillis + JwtUtil.JWT_TTL;
        Date expDate = new Date(expMillis);
        return Jwts.builder()
                .id(uuid)
                .subject(subject)
                .issuer("sg")
                .issuedAt(now)
                .signWith(secretKey)
                .expiration(expDate);
    }

    public static SecretKey generalKey() {
        byte[] encodeKey = Base64.getDecoder().decode(JwtUtil.JWT_KEY);
        return new SecretKeySpec(encodeKey, 0, encodeKey.length, "HmacSHA256");
    }

    public static Claims parseJWT(String jwt) {
        SecretKey secretKey = generalKey();
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }

    public static Integer getUserId(String string) {
        int userid;
        try {
            Claims claims = JwtUtil.parseJWT(string);
            userid = Integer.parseInt(claims.getSubject());
        } catch (Exception e) {
            return -1;
        }
        return userid;
    }
}
