package com.project.taskmanager.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Allow credentials
        config.setAllowCredentials(true);

        // Allow specific origins (frontend URLs)
        config.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",
                "http://localhost:5173", // Vite default port
                "http://127.0.0.1:3000",
                "http://127.0.0.1:5173"
        ));

        // Allow all headers
        config.setAllowedHeaders(Arrays.asList(
                "Origin",
                "Content-Type",
                "Accept",
                "Authorization",
                "X-User-Id",
                "X-Requested-With"
        ));

        // Allow all HTTP methods
        config.setAllowedMethods(Arrays.asList(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "OPTIONS",
                "PATCH"
        ));

        // How long the response from a pre-flight request can be cached
        config.setMaxAge(3600L);

        // Expose headers
        config.setExposedHeaders(Arrays.asList(
                "X-User-Id",
                "Content-Type"
        ));

        source.registerCorsConfiguration("/api/**", config);

        return new CorsFilter(source);
    }
}