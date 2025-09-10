package com.project.ecom.security;

import com.project.ecom.security.jwt.AuthTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
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
//    @Bean
//    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http.csrf(AbstractHttpConfigurer::disable);  // disabling csrf
//        http.authorizeHttpRequests((requests) -> requests
//                .requestMatchers("/", "/signin", "/register",
////                        "/api/public/categories", // commented for testing
//                        "/api/public/products",
//                        "/api/public/categories/{categoryId}/products",
//                        "/api/public/products/keyword/{keyword}").permitAll()
//                .anyRequest().authenticated());
//
//        requestMatchers(HttpMethod.GET, "/", "/signin", "/register",
//                "/api/public/products",
//                "/api/public/categories/{categoryId}/products",
//                "/api/public/products/keyword/{keyword}").permitAll()
//
//        http.addFilterBefore(authTokenFilter,BasicAuthenticationFilter.class);
//        http.httpBasic(withDefaults());
//        return http.build();
//    }


    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);  // disabling csrf
        http.authorizeHttpRequests((requests) -> requests
                // All Accessible By Only ADMIN
                .requestMatchers("/api/categories", "/api/categories/*",
                        "/api/carts", "/api/products/count", "/api/addresses").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/address/id/*").hasRole("ADMIN")
                // All Accessible By Only SELLER
                .requestMatchers(HttpMethod.PUT, "/api/products/*").hasRole("SELLER")
                .requestMatchers("/api/categories/*/product",
                        "/api/products/*/image").hasRole("SELLER")
                // All Accessible By Only ADMIN/SELLER
                .requestMatchers(HttpMethod.DELETE, "/api/products/*").hasAnyRole("SELLER", "ADMIN")
                // All accessible by user:
//                .requestMatchers("/api/carts/**", "/api/address", "/api/address/user", "/api/address/*").hasRole("USER")   // /api/carts/** -> protects everything under /api/carts/
                .requestMatchers("/api/carts/**", "/api/address", "/api/address/*", "/api/order/users/payment").hasRole("USER")   // /api/carts/** -> protects everything under /api/carts/
                // All Public Requests:
                .requestMatchers("/", "/signin", "register",
                        "/api/public/categories", "/api/public/products",
                        "/api/public/categories/*/products", "/api/public/products/keyword/*").permitAll()
                .anyRequest().authenticated());

        // If we want to protect routes against a type of request then:
        // requestMatchers(HttpMethod.GET, route1, route2...).permitAll()

        http.addFilterBefore(authTokenFilter,BasicAuthenticationFilter.class);
        http.httpBasic(withDefaults());
        return http.build();
    }
}
