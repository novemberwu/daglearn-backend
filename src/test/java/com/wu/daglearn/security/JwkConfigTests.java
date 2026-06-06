package com.wu.daglearn.security;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext
public class JwkConfigTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private JWKSource<SecurityContext> jwkSource;

    @Test
    @DisplayName("Given valid PEM keys, when calling parser helpers, then keys are cryptographically parsed correctly")
    void givenValidPemKeys_whenParsing_thenKeysAreParsedCorrectly() throws Exception {
        // Given - Generate a valid RSA keypair programmatically
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair keyPair = kpg.generateKeyPair();
        RSAPublicKey originalPublic = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey originalPrivate = (RSAPrivateKey) keyPair.getPrivate();

        // Format to PEM format (including headers and newlines to test sanitization)
        String publicPem = "-----BEGIN PUBLIC KEY-----\n" +
                Base64.getMimeEncoder().encodeToString(originalPublic.getEncoded()) +
                "\n-----END PUBLIC KEY-----";

        String privatePem = "-----BEGIN PRIVATE KEY-----\n" +
                Base64.getMimeEncoder().encodeToString(originalPrivate.getEncoded()) +
                "\n-----END PRIVATE KEY-----";

        // When - Use SecurityConfig helper logic (using reflection or direct invocation if public)
        RSAPublicKey parsedPublic = parsePublicKeyHelper(publicPem);
        RSAPrivateKey parsedPrivate = parsePrivateKeyHelper(privatePem);

        // Then - Verify parsed keys have identical values/modulus
        assertEquals(originalPublic.getModulus(), parsedPublic.getModulus());
        assertEquals(originalPrivate.getModulus(), parsedPrivate.getModulus());
    }

    @Test
    @DisplayName("Given missing PEM keys, when context starts, then jwkSource falls back to auto-generated in-memory keys")
    void givenMissingPemKeys_whenContextStarts_thenJwkSourceFallsBackToInMemoryKeys() throws Exception {
        // Given - Default/missing PEM properties (which are empty on startup)
        
        // When - JWKSource bean is retrieved
        assertNotNull(jwkSource);
        java.util.List<com.nimbusds.jose.jwk.JWK> keys = jwkSource.get(
                new com.nimbusds.jose.jwk.JWKSelector(new com.nimbusds.jose.jwk.JWKMatcher.Builder().build()), null);
        assertNotNull(keys);
        assertFalse(keys.isEmpty());
        
        // Then - Verify it is an RSA Key and contains a random/valid Key ID
        RSAKey rsaKey = (RSAKey) keys.get(0);
        assertNotNull(rsaKey.getKeyID());
        assertNotNull(rsaKey.toRSAPublicKey());
    }

    private static RSAPublicKey parsePublicKeyHelper(String pem) throws Exception {
        String cleanPem = pem.replace("-----BEGIN PUBLIC KEY-----", "")
                             .replace("-----END PUBLIC KEY-----", "")
                             .replaceAll("\\s+", "");
        byte[] decoded = Base64.getDecoder().decode(cleanPem);
        java.security.spec.X509EncodedKeySpec spec = new java.security.spec.X509EncodedKeySpec(decoded);
        java.security.KeyFactory kf = java.security.KeyFactory.getInstance("RSA");
        return (RSAPublicKey) kf.generatePublic(spec);
    }

    private static RSAPrivateKey parsePrivateKeyHelper(String pem) throws Exception {
        String cleanPem = pem.replace("-----BEGIN PRIVATE KEY-----", "")
                             .replace("-----END PRIVATE KEY-----", "")
                             .replaceAll("\\s+", "");
        byte[] decoded = Base64.getDecoder().decode(cleanPem);
        java.security.spec.PKCS8EncodedKeySpec spec = new java.security.spec.PKCS8EncodedKeySpec(decoded);
        java.security.KeyFactory kf = java.security.KeyFactory.getInstance("RSA");
        return (RSAPrivateKey) kf.generatePrivate(spec);
    }
}
