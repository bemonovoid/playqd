package com.bemonovoid.playqd.security.jwt;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import com.bemonovoid.playqd.core.model.TokenUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

public class JwtService {

    private final SecretKey secretKey;

    public JwtService(String tokenSecret) {
        this.secretKey = Keys.hmacShaKeyFor(tokenSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String create(Authentication authentication) {
        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Claims claims = Jwts.claims().setSubject(authentication.getName());
        claims.put("userId", authentication.getName());
        claims.put("role", roles);

        Date expirationDate = Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant());

        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(expirationDate)
                .signWith(secretKey)
                .compact();
    }

    public UserDetails parse(String authToken) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(authToken)
                    .getBody();

            String roles = (String) claims.get("role");

            GrantedAuthority[] authorities = Arrays.stream(roles.split(","))
                    .map(SimpleGrantedAuthority::new)
                    .toArray(SimpleGrantedAuthority[]::new);

            return new TokenUserDetails(authToken, User.builder()
                    .username(claims.getSubject())
                    .password(authToken)
                    .authorities(authorities)
                    .build());
        } catch (ExpiredJwtException e) {
            throw new CredentialsExpiredException("Token expired.", e);
        } catch (IllegalArgumentException | MalformedJwtException e) {
            throw new BadCredentialsException("Invalid token", e);
        } catch (SignatureException e) {
            throw new AuthenticationServiceException("Signature problem.", e);
        } catch (Exception e) {
            throw new AuthenticationServiceException("Cannot parse token", e);
        }
    }
}
