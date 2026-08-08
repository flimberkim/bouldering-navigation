package com.boulderingnavigation.config;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Exposes a {@link CorsConfigurationSource} bean rather than implementing
 * {@code WebMvcConfigurer.addCorsMappings}, because Spring Security's filter
 * chain runs before Spring MVC's CORS handling — a CORS mapping registered
 * only at the MVC level never gets a chance to answer the browser's preflight
 * OPTIONS request, which Security would otherwise reject as unauthenticated.
 * {@code SecurityConfig} enables {@code .cors()}, which picks this bean up
 * and answers preflight requests before authorization runs.
 */
@Configuration
public class WebConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
}
