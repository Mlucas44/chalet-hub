// JwtAuthFilter.java
package com.chaletHub.backend.security;

import com.chaletHub.backend.model.UserAccount;
import com.chaletHub.backend.repository.UserAccountRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
  private final JwtService jwt;
  private final UserAccountRepository users;

  public JwtAuthFilter(JwtService jwt, UserAccountRepository users) {
    this.jwt = jwt;
    this.users = users;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws ServletException, IOException {

    String auth = req.getHeader("Authorization");
    if (auth != null && auth.startsWith("Bearer ")) {
      try {
        var jws = jwt.parse(auth.substring(7));
        Claims claims = jws.getBody();
        String email = claims.getSubject();
        UserAccount user = users.findByEmail(email).orElse(null);
        if (user != null) {
          var authorities = user.getRoles().stream()
              .map(r -> new SimpleGrantedAuthority(r.name()))
              .toList();
          var authentication = new UsernamePasswordAuthenticationToken(email, null, authorities);
          SecurityContextHolder.getContext().setAuthentication(authentication);
        }
      } catch (Exception ignored) {
        // token invalide -> on laisse passer (routes publiques)
      }
    }
    chain.doFilter(req, res);
  }
}
