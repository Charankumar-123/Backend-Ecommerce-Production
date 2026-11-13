package com.eoxys.crud;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.eoxys.service.JWTService;

public class JWTServiceTest {

    @Test
    public void testTokenGenerationAndExtraction() {
        JWTService jwtService = new JWTService();
        String testEmail = "operator@warehouse.com";
        String testRole = "WAREHOUSE";

        String token = jwtService.generateToken(testEmail, testRole);
        assertNotNull(token);

        String extractedEmail = jwtService.extractEmail(token);
        String extractedRole = jwtService.extractRole(token);

        assertEquals(testEmail, extractedEmail);
        assertEquals(testRole, extractedRole);
    }
}

