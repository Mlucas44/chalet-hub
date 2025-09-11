package com.chalethub.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/**").permitAll() // Ouvre l’API
            .anyRequest().permitAll() // Ouvre tout le reste (dev)
        )
        .formLogin(form -> form.disable()) // Supprime la page /login
        .httpBasic(basic -> basic.disable());
    return http.build();
  }
}
