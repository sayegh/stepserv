package com.thinkboxberlin.stepserv.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.thinkboxberlin.stepserv.exception.IdentityVerificationFailedException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class IdentityVerificationServiceIntegrationTest {
    static final String BASE_URL = "https://api.secondlife.com";

    @Autowired
    private IdentityVerificationService identityVerificationService;

    @Test
    public void shouldVerifyIdentity() throws IdentityVerificationFailedException {
        // Checking for existent agent.
        identityVerificationService.verifyIdentity(BASE_URL, "Vito", "Dean",
            "b49ba0a8-4e0f-4cd3-95cb-aac4eeb41579");
    }

    @Test
    public void shouldDetectBogusIdentity() {
        // Checking for non-existent agent.
        assertThrows(IdentityVerificationFailedException.class,
            () -> identityVerificationService.verifyIdentity(BASE_URL, "BogusTheSchmogus", "Resident",
            "b49ba0a8-4e0f-4cd3-95cb-aac4eeb41579")
        );
    }

    @Test
    public void shouldDetectMismatch() {
        // Checking with wrong UUID.
        assertThrows(IdentityVerificationFailedException.class,
            () -> identityVerificationService.verifyIdentity(BASE_URL, "Vito", "Dean",
                "b49ba0a8-0000-4cd3-95cb-aac4eeb41579")
        );
    }
}
