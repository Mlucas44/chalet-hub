package com.chaletHub.backend.config;

import com.chaletHub.backend.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // 👈
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; // 👈
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableMethodSecurity // 👈 permet @PreAuthorize sur les contrôleurs
public class SecurityConfig {

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  SecurityFilterChain api(HttpSecurity http, JwtAuthFilter jwt) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .cors(cors -> cors.configurationSource(request -> {
          var c = new CorsConfiguration();
          c.setAllowedOrigins(List.of("http://localhost:5173"));
          c.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
          c.setAllowedHeaders(List.of("*"));
          c.setAllowCredentials(true);
          return c;
        }))
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(reg -> reg
            // Swagger & OpenAPI
            .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

            // Auth
            .requestMatchers("/api/auth/**").permitAll()

            // Catalogue chalets : lecture publique / écriture admin
            .requestMatchers(HttpMethod.GET, "/api/chalets/**").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/chalets/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.PUT, "/api/chalets/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/api/chalets/**").hasRole("ADMIN")

            // Admin back-office
            .requestMatchers("/api/admin/**").hasRole("ADMIN")

            // Réservations : besoin d’être connecté
            .requestMatchers("/api/bookings/**").authenticated()

            // Tout le reste : interdit par défaut
            .anyRequest().denyAll())
        .addFilterBefore(jwt, BasicAuthenticationFilter.class)
        // Utile pour tester rapidement avec “Authorize: Basic …” ; tu peux l’enlever
        // plus tard
        .httpBasic(Customizer.withDefaults());

    return http.build();
  }
}
