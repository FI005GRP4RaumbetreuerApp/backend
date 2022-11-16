package org.gso.backend.security;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.gso.backend.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${app.access_token_secret}")
    private String access_token_secret;
    @Value("${app.refresh_token_secret}")
    private String refresh_token_secret;

    public String generateToken(User user) {
        Instant now = Instant.now();
        Instant expiration = now.plus(15, ChronoUnit.MINUTES);

        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiration))
                .claim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .signWith(SignatureAlgorithm.HS512, access_token_secret)
                .compact();

    }

    public String generateRefreshToken(User user){
        Instant now = Instant.now();
        Instant expiration = now.plus(180, ChronoUnit.DAYS);

        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiration))
                .signWith(SignatureAlgorithm.HS512, refresh_token_secret)
                .compact();
    }

    public String getUserEmailFromAccessToken(String token) {
        if(token.startsWith("Bearer")){
            token = token.substring(7);
        }

        Claims claims = Jwts
                .parser()
                .setSigningKey(access_token_secret)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public String getUserEmailFromRefreshToken(String token) {
        if(token.startsWith("Bearer")){
            token = token.substring(7);
        }

        Claims claims = Jwts
                .parser()
                .setSigningKey(refresh_token_secret)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public boolean is_access_token_expired_but_valid(String token){
        try {
            Jwts.parser().setSigningKey(access_token_secret).parseClaimsJws(token);
        } catch (ExpiredJwtException ex){
            return true;
        }

        return false;
    }

    public boolean validate_access_token(String token) {
        try {
            Jwts.parser().setSigningKey(access_token_secret).parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
        }

        return false;
    }

    public boolean validate_refresh_token(String token) {
        try {
            Jwts.parser().setSigningKey(refresh_token_secret).parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
        }

        return false;
    }

}