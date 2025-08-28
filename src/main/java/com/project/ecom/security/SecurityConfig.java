package com.project.ecom.security;

import com.project.ecom.security.jwt.AuthTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Service;

import static org.springframework.security.config.Customizer.withDefaults;

@Service
public class SecurityConfig {
    @Autowired
    private AuthTokenFilter authTokenFilter;
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);  // disabling csrf
        http.authorizeHttpRequests((requests) -> requests
                .requestMatchers("/", "/signin", "/register",
//                        "/api/public/categories", // commented for testing
                        "/api/public/products",
                        "/api/public/categories/{categoryId}/products",
                        "/api/public/products/keyword/{keyword}").permitAll()
                .anyRequest().authenticated());

//        requestMatchers(HttpMethod.GET, "/", "/signin", "/register",
//                "/api/public/products",
//                "/api/public/categories/{categoryId}/products",
//                "/api/public/products/keyword/{keyword}").permitAll()

        http.addFilterBefore(authTokenFilter,BasicAuthenticationFilter.class);
        http.httpBasic(withDefaults());
        return http.build();
    }
}
