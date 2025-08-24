package com.project.ecom.controllers;

import com.project.ecom.payload.LoginRequestDTO;
import com.project.ecom.payload.LoginResponseDTO;
import com.project.ecom.security.UserDetailsServiceImpl;
import com.project.ecom.security.jwt.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UsersController {
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTUtils jwtUtils;
    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody LoginRequestDTO requestDTO) {
        String username = requestDTO.getUsername();
        String password = requestDTO.getPassword();

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (userDetails!=null && passwordEncoder.matches(password, userDetails.getPassword())) {
            String jwt = jwtUtils.generateJWTTokenFromUsername(userDetails);
            LoginResponseDTO responseDTO = new LoginResponseDTO(jwt, "Login Successful");

            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}
