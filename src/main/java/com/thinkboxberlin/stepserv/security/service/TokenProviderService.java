package com.thinkboxberlin.stepserv.security.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.thinkboxberlin.stepserv.security.model.User;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TokenProviderService {
    @Value("${security.jwt.token.secret-key}")
    private String JWT_SECRET;

    public String generateAccessToken(User user) {
        Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET);
        return JWT.create()
            .withSubject(user.getUsername())
            .withClaim("username", user.getUsername())
            .withExpiresAt(genAccessExpirationDate())
            .sign(algorithm);
    }

    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET);
            return JWT.require(algorithm)
                .build()
                .verify(token)
                .getSubject();
        } catch (final JWTVerificationException exception) {
            throw new JWTVerificationException("Error while validating token", exception);
        }
    }

    private Instant genAccessExpirationDate() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}
