package com.project.ecom.security;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Service;

import static org.springframework.security.config.Customizer.withDefaults;

@Service
public class SecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);  // disabling csrf
        http.authorizeHttpRequests((requests) -> requests
                .requestMatchers("/", "/signin",
//                        "/api/public/categories", // commented for testing
                        "/api/public/products",
                        "/api/public/categories/{categoryId}/products",
                        "/api/public/products/keyword/{keyword}").permitAll()
                .anyRequest().authenticated());
        http.httpBasic(withDefaults());
        return http.build();
    }
}
