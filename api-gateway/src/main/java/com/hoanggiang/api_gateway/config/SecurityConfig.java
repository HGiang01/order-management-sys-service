package com.hoanggiang.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {
    String[] publicAPIs = {"/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/api-docs/**", "/swagger-resources/**", "/aggregate/**"};

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.cors(Customizer.withDefaults())
                           .authorizeHttpRequests(authorize -> authorize.requestMatchers(publicAPIs)
                                                                        .permitAll()
                                                                        .anyRequest()
                                                                        .authenticated())
                           .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                           .build();
    }

    // Configuration fo CORS
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        // Tạo một config
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("*"));
        // Chỉ được gửi lên header Authorization và Content-Type
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        // Cho phép client đọc được Authorization từ server trả về
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Mọi request qua bất kì endpoints nào (/**) đều áp dụng quy tác trên
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
