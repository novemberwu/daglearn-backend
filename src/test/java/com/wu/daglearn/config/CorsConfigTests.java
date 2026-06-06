package com.wu.daglearn.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "app.security.cors.allowed-origins=http://localhost:3000,https://daglearn.vercel.app"
})
public class CorsConfigTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void givenMultipleAllowedOrigins_whenPreflightFromLocalhost_thenCorsHeadersAreReturned() throws Exception {
        // Given - "http://localhost:3000" is configured in allowed origins

        // When - Preflight (OPTIONS) request is sent from localhost origin
        mockMvc.perform(options("/api/courses")
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "GET"))
                // Then - CORS headers are returned with localhost
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"))
                .andExpect(header().string("Access-Control-Allow-Credentials", "true"));
    }

    @Test
    void givenMultipleAllowedOrigins_whenPreflightFromVercel_thenCorsHeadersAreReturned() throws Exception {
        // Given - "https://daglearn.vercel.app" is configured in allowed origins

        // When - Preflight (OPTIONS) request is sent from Vercel origin
        mockMvc.perform(options("/api/courses")
                .header("Origin", "https://daglearn.vercel.app")
                .header("Access-Control-Request-Method", "GET"))
                // Then - CORS headers are returned with Vercel origin
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "https://daglearn.vercel.app"))
                .andExpect(header().string("Access-Control-Allow-Credentials", "true"));
    }

    @Test
    void givenMultipleAllowedOrigins_whenPreflightFromUntrustedOrigin_thenPreflightIsRejected() throws Exception {
        // Given - "https://untrusted.com" is not in the allowed origins

        // When - Preflight (OPTIONS) request is sent from untrusted origin
        mockMvc.perform(options("/api/courses")
                .header("Origin", "https://untrusted.com")
                .header("Access-Control-Request-Method", "GET"))
                // Then - preflight request is rejected
                .andExpect(status().isForbidden());
    }
}
