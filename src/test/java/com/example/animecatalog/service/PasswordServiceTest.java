package com.example.animecatalog.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
/**
 * Author: cyqatester
 */
class PasswordServiceTest {
    private final PasswordService service = new PasswordService();

    @Test
    void hashesAndVerifiesPassword() {
        String hash = service.hash("sample-password");
        assertNotEquals("sample-password", hash);
        assertTrue(service.matches("sample-password", hash));
        assertFalse(service.matches("wrong-password", hash));
    }
}
