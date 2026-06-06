package com.wu.daglearn.security;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class JwkConfigPropertiesTests {

    private static String expectedKeyId = "test-persistent-key-id";
    private static RSAPublicKey originalPublic;

    @BeforeAll
    static void setupKeys() throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair keyPair = kpg.generateKeyPair();
        originalPublic = (RSAPublicKey) keyPair.getPublic();

        String publicPem = "-----BEGIN PUBLIC KEY-----\n" +
                Base64.getMimeEncoder().encodeToString(originalPublic.getEncoded()) +
                "\n-----END PUBLIC KEY-----";

        String privatePem = "-----BEGIN PRIVATE KEY-----\n" +
                Base64.getMimeEncoder().encodeToString(keyPair.getPrivate().getEncoded()) +
                "\n-----END PRIVATE KEY-----";

        // Set them as system properties so Spring's @SpringBootTest active environment loads them
        System.setProperty("JWT_PRIVATE_KEY_PEM", privatePem);
        System.setProperty("JWT_PUBLIC_KEY_PEM", publicPem);
        System.setProperty("JWT_KEY_ID", expectedKeyId);
    }

    @AfterAll
    static void teardown() {
        System.clearProperty("JWT_PRIVATE_KEY_PEM");
        System.clearProperty("JWT_PUBLIC_KEY_PEM");
        System.clearProperty("JWT_KEY_ID");
    }

    @Autowired
    private JWKSource<SecurityContext> jwkSource;

    @Test
    @DisplayName("Given persistent keys in environment, when context loads, then JWKSource contains the persistent key and custom key ID")
    void givenPersistentKeys_whenContextLoads_thenJwkSourceUsesThem() throws Exception {
        // Given - Keys are set in system properties before startup (see setupKeys)

        // When - Fetch JWK list from the injected bean
        assertNotNull(jwkSource);
        java.util.List<com.nimbusds.jose.jwk.JWK> keys = jwkSource.get(
                new com.nimbusds.jose.jwk.JWKSelector(new com.nimbusds.jose.jwk.JWKMatcher.Builder().build()), null);
        assertNotNull(keys);
        assertFalse(keys.isEmpty());

        // Then - Verify it contains our custom key ID and modulus matches
        RSAKey rsaKey = (RSAKey) keys.get(0);
        assertEquals(expectedKeyId, rsaKey.getKeyID());
        assertEquals(originalPublic.getModulus(), rsaKey.toRSAPublicKey().getModulus());
    }
}
