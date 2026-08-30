package com.ner.logistics.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

public class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        // 256-bit secret key for HMAC-SHA256 requirement
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", "NERLogisticsSuperSecretKey2026NorthEastRegionSmartAccessibilityPlatformSecretKey");
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationMs", 86400000L); // 24 hours
    }

    @Test
    void testTokenGenerationAndValidation() {
        String email = "driver@sih.gov.in";
        String role = "DRIVER";

        String token = jwtTokenProvider.generateToken(email, role);

        assertNotNull(token);
        assertTrue(token.length() > 20);

        boolean isValid = jwtTokenProvider.validateToken(token);
        assertTrue(isValid, "Generated JWT token should be valid");

        String extractedEmail = jwtTokenProvider.getEmailFromToken(token);
        assertEquals(email, extractedEmail, "Extracted email should match original subject");
    }

    @Test
    void testInvalidTamperedTokenValidation() {
        String email = "officer@sih.gov.in";
        String token = jwtTokenProvider.generateToken(email, "FIELD_OFFICER");

        String tamperedToken = token + "invalid_signature";

        boolean isValid = jwtTokenProvider.validateToken(tamperedToken);
        assertFalse(isValid, "Tampered JWT token should be rejected");
    }
}
