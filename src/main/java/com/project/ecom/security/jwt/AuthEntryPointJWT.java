package com.project.ecom.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/* Provides custom handling for unauthorized requests, typically when authentication is required
but not supplied or valid.
 */
public class AuthEntryPointJWT implements AuthenticationEntryPoint {
    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJWT.class);
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        logger.error("Authentication Error: {}", authException.getMessage());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        final Map<String, Object> body = new HashMap<>();
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("error", "Unauthorized");
        body.put("message", authException.getMessage());
        body.put("path", request.getServletPath());

        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);
    }
}

/*
* In above, we are having our custom AuthenticationEntryPoint, which Spring Security
* calls whenever an unauthorized request happens (e.g., invalid/missing JWT).
*
* (Q) What is final ObjectMapper mapper = new ObjectMapper()?
*     - ObjectMapper is from Jackson (JSON library). It converts Java objects (maps, POJOs,
*       lists, etc.) into JSON strings.
*     - mapper.writeValue(response.getOutputStream(), body); Takes our body map (which has
*       status, error, message, path). Serializes it to JSON. Writes that JSON directly into
*       the HTTP response output stream.
*     - So instead of sending back just plain text (User not authorized), we’re sending a
*       structured JSON error response.
* */
