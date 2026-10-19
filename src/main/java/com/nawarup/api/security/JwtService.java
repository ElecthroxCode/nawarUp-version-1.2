package com.nawarup.api.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.nawarup.api.user.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Date;

@Service
public class JwtService {
    private final Algorithm algorithm;
    private final JWTVerifier verifier;
    private final long expirationMs;

    public JwtService(@Value("${app.jwt.secret}") String secretBase64,
                      @Value("${app.jwt.expiration-ms}") long expirationMs) {
        // Auth0 espera los bytes directos del secret descodificado de Base64
        byte[] secretBytes = Base64.getDecoder().decode(secretBase64);
        this.algorithm = Algorithm.HMAC256(secretBytes);
        this.verifier = JWT.require(algorithm).build();
        this.expirationMs = expirationMs;
    }

    public String generate(User user) {
        Date now = new Date();
        return JWT.create()
                .withSubject(user.getEmail())
                .withClaim("uid", user.getId())
                .withClaim("role", user.getRole().name())
                .withIssuedAt(now)
                .withExpiresAt(new Date(now.getTime() + expirationMs))
                .sign(algorithm);
    }

    public DecodedJWT parse(String token) {
        return verifier.verify(token);
    }
}