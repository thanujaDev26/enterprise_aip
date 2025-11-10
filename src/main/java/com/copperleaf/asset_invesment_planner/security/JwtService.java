package com.copperleaf.asset_invesment_planner.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    private final Key signingKey;
    private final long ttlMillis;


    public JwtService(@Value("${security.jwt.secret}") String secret,
                      @Value("${security.jwt.ttl}") long ttlMillis){
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.ttlMillis = ttlMillis;
    }

    public String generateToken(String subject, Map<String, Object> claims) {
        long now = System.currentTimeMillis();
        return Jwts.builder().setClaims(claims).setSubject(subject)
                .setIssuedAt(new Date(now)).setExpiration(new Date(now+ttlMillis))
                .signWith(signingKey, SignatureAlgorithm.HS256).compact();
    }

//    public String getSubject(String token){
//        return Jwts.parserBuilder().setSigningKey(signingKey).build()
//                .parseClaimsJws(token).getBody().getSubject();
//    }

    public String getSubject(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }



}
