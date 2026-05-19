package com.sistemabarberia.fadex_backend.auth.security.jwt;



import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

import io.jsonwebtoken.io.Decoders;

import javax.crypto.SecretKey;
@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {


    private final JwtProperties properties ;


    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(properties.getSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


    public String generateToken(UserDetails user) {



        List<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).filter(
                        auth -> auth.startsWith("ROLE_")
                ).toList();
        List<String> permisos = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).filter(
                        auth -> !auth.startsWith("ROLE_")
                ).toList();
        return
                Jwts.builder().
                        subject(user.getUsername())
                        .claim("roles", roles)
                        .claim("permisos", permisos)

                        .issuedAt(new Date())
                        .expiration(new Date(System.currentTimeMillis() + properties.getExpiration())).
                        signWith(getSigningKey())
                        .compact();
    }



    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(extractAllClaims(token));
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = extractAllClaims(token);


            return claims.getExpiration().after(new Date());

        } catch (JwtException | IllegalArgumentException e) {
            log.error("JWT inválido: {}", e.getMessage());
            return false;
        }
    }

}
