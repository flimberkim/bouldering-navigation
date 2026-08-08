package com.boulderingnavigation.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * Runs before DispatcherServlet, so it can't go through
 * {@link com.boulderingnavigation.exception.GlobalExceptionHandler}. Writes
 * the same plain-text-body style as every other error response in this API.
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("text/plain;charset=UTF-8");
        response.getWriter().write("인증이 필요합니다.");
    }
}
