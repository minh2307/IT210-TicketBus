// === SecurityConfig.java ===
package com.example.it210ticketbus.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Configuration for password encoding using BCrypt
 */
@Configuration
public class SecurityConfig {
    
    /**
     * Bean for password encoding using BCrypt
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
