package com.kt.security;

import com.kt.common.exception.CustomException;
import com.kt.common.exception.ErrorCode;
import com.kt.domain.user.User;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long accessTokenValidityInMs;
    private final long refreshTokenValidityInMs;

    public JwtTokenProvider(@Value("${kt.jwt.secret:kt-cloud-tech-up-shopping-202511171107}") String secret,
                            @Value("${kt.jwt.access-token-expiration:300000}") long accessTokenValidityInMs,
                            @Value("${kt.jwt.refresh-token-expiration:43200000}") long refreshTokenValidityInMs
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenValidityInMs = accessTokenValidityInMs;
        this.refreshTokenValidityInMs = refreshTokenValidityInMs;
    }

    public String generateAccessToken(User user){
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenValidityInMs);

        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .claim("loginId",user.getLoginId())
                .claim("role",user.getRole().name())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key,Jwts.SIG.HS256)
                .compact();
    }

    public String generateRefreshToken(User user){
        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshTokenValidityInMs);

        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key,Jwts.SIG.HS256)
                .compact();
    }

    public void validateToken(String token){
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
        } catch (MalformedJwtException | DecodingException | UnsupportedJwtException e) {
            throw new CustomException(ErrorCode.MALFORMED_JWT_TOKEN);
        } catch (ExpiredJwtException e) {
            throw new CustomException(ErrorCode.EXPIRED_JWT_TOKEN);
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_JWT_TOKEN);
        }
    }


    public Long getUserId(String token) {
        var claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return Long.valueOf(claims.getSubject());
    }

    public LocalDateTime getRefreshTokenExpiryDateTime() {
        return LocalDateTime.ofInstant(
                Instant.now().plusMillis(refreshTokenValidityInMs),
                ZoneId.systemDefault()
        );
    }

}
