package com.wu.daglearn.security;

import com.wu.daglearn.model.User;
import com.wu.daglearn.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class Neo4jUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public Neo4jUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // We use email as the principal 'username' in this system
        User user = userRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + username));

        return new org.springframework.security.core.userdetails.User(
                user.getId(),
                user.getPassword(),
                Collections.emptyList() // No roles implemented yet for MVP
        );
    }
}
