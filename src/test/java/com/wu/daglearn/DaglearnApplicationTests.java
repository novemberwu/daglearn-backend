package com.wu.daglearn;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class DaglearnApplicationTests {

    @Value("${spring.neo4j.uri}")
    private String neo4jUri;

    @Value("${app.security.oauth2.client-id}")
    private String clientId;

    @Value("${app.security.oauth2.client-secret}")
    private String clientSecret;

    @Value("${app.security.oauth2.redirect-uri}")
    private String redirectUri;

    @Value("${app.security.oauth2.post-logout-redirect-uri}")
    private String postLogoutRedirectUri;

    @Value("${app.security.jwt.issuer}")
    private String jwtIssuer;

    @Value("${app.security.cors.allowed-origins}")
    private String allowedOrigins;

    @Test
    void contextLoads() {
    }

    @Test
    void givenSpringContext_whenLoadingProperties_thenPropertyPlaceholderDefaultsAreInjectedCorrectly() {
        // Given - Spring Context loads

        // When/Then - Verify that the property placeholder default values are successfully injected
        assertEquals("bolt://localhost:7687", neo4jUri);
        assertEquals("next-client", clientId);
        assertEquals("secret", clientSecret);
        assertEquals("http://localhost:3000/api/auth/callback/next-learn", redirectUri);
        assertEquals("http://localhost:3000/", postLogoutRedirectUri);
        assertEquals("http://localhost:8080", jwtIssuer);
        assertEquals("http://localhost:3000", allowedOrigins);
    }
}
