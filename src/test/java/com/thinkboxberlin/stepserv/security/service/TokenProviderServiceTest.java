package com.thinkboxberlin.stepserv.security.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.thinkboxberlin.stepserv.security.authentication.UserRole;
import com.thinkboxberlin.stepserv.security.model.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class TokenProviderServiceTest {
    @Autowired
    private TokenProviderService tokenProviderService;

    @Test
    public void shouldCreateValidAccessToken() {
        final User user = new User("", "", UserRole.USER);
        final String token = tokenProviderService.generateAccessToken(user);
        assertTrue(token.startsWith("ey"));
    }

    @Test
    public void shouldDetectInvalidToken() {
        assertThrows(JWTVerificationException.class, () -> tokenProviderService.validateToken("invalid token"));
    }
}
