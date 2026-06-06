package com.wu.daglearn.security;

import com.wu.daglearn.model.User;
import com.wu.daglearn.repository.UserRepository;
import com.wu.daglearn.security.dto.AuthResponse;
import com.wu.daglearn.security.dto.LoginRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtEncoder encoder;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.security.jwt.issuer}")
    private String jwtIssuer;

    public AuthController(JwtEncoder encoder, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.encoder = encoder;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        System.out.println("Login attempt for email: " + loginRequest.getEmail());
        
        // Try to find by ID (which we set as email in DataLoader)
        User user = userRepository.findById(loginRequest.getEmail())
                .orElse(null);

        if (user == null) {
            // Fallback: search all users for matching email field (if ID is different)
            user = userRepository.findAll().stream()
                    .filter(u -> loginRequest.getEmail().equalsIgnoreCase(u.getEmail()))
                    .findFirst()
                    .orElse(null);
        }

        if (user == null) {
            System.out.println("User not found: " + loginRequest.getEmail());
            return ResponseEntity.status(401).body(java.util.Map.of("error", "Invalid credentials"));
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            System.out.println("Password mismatch for user: " + loginRequest.getEmail());
            return ResponseEntity.status(401).body(java.util.Map.of("error", "Invalid credentials"));
        }

        System.out.println("Login successful for user: " + loginRequest.getEmail());
        Instant now = Instant.now();
        long expiry = 3600L; // 1 hour

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(jwtIssuer)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiry))
                .subject(user.getId())
                .claim("scope", "topic:read")
                .build();

        String token = this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        return ResponseEntity.ok(new AuthResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                token
        ));
    }
}
