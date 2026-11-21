package com.bookmgt.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Component
public class JwtUtil {

    /** 默认密钥：Base64 编码的 32 字节; 生产环境请通过配置覆盖 */
    private static final String DEFAULT_SECRET =
            "jAzKB7F4+IFZJEEk283FUMbahEKYhG7UJ3uI8HnisK4";
    /** 默认有效期：24 小时（毫秒） */
    private static final long DEFAULT_EXPIRATION_MS = 86_400_000L;
    /** 默认签发者 */
    private static final String DEFAULT_ISSUER = "default-issuer";

    @Value("${jwt.secret:" + DEFAULT_SECRET + "}")
    private String secret;

    @Value("${jwt.expiration-ms:" + DEFAULT_EXPIRATION_MS + "}")
    private long expirationMs;

    @Value("${jwt.issuer:" + DEFAULT_ISSUER + "}")
    private String issuer;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        // Base64 解码并校验密钥强度（32 字节 → HS256）
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    // ────── 生成 Token ──────

    /** 仅 subject */
    public String generateToken(String subject) {
        return generateToken(subject, null);
    }

    /** 仅自定义 claims */
    public String generateToken(Map<String, Object> claims) {
        return generateToken(null, claims);
    }

    /** subject + 自定义 claims */
    public String generateToken(String subject, Map<String, Object> claims) {
        Date now = new Date();
        JwtBuilder builder = Jwts.builder()
                .id(UUID.randomUUID().toString())            // jti
                .issuer(issuer)                              // iss
                .issuedAt(now)                               // iat
                .expiration(new Date(now.getTime() + expirationMs)) // exp
                .signWith(secretKey);
        if (claims != null && !claims.isEmpty()) {
            builder.claims(claims);
        }
        if (subject != null && !subject.isBlank()) {
            builder.subject(subject);
        }
        return builder.compact();
    }

    // ────── 解析与校验 ──────

    /** 解析 token，无效或过期抛 JwtException */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /** 安全解析：无效或过期返回 null，不抛异常 */
    public Claims parseTokenQuietly(String token) {
        try {
            return parseToken(token);
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    /** 是否有效：签名正确且未过期 */
    public boolean isValid(String token) {
        return parseTokenQuietly(token) != null;
    }

    /** 是否已过期 */
    public boolean isExpired(String token) {
        try {
            return parseToken(token).getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return true;
        }
    }

    // ────── 提取字段 ──────

    public String getSubject(String token) {
        return parseToken(token).getSubject();
    }

    /** 获取全部 claims */
    public Claims getClaims(String token) {
        return parseToken(token);
    }

    public Date getExpiration(String token) {
        return parseToken(token).getExpiration();
    }

    public String getJti(String token) {
        return parseToken(token).getId();
    }

    public String getIssuer(String token) {
        return parseToken(token).getIssuer();
    }
}