package com.chaletHub.backend.controller;

import com.chaletHub.backend.model.Role;
import com.chaletHub.backend.model.UserAccount;
import com.chaletHub.backend.repository.UserAccountRepository;
import com.chaletHub.backend.security.JwtService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@Tag(name = "Auth")
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {
  private final UserAccountRepository users;
  private final PasswordEncoder encoder;
  private final JwtService jwt;

  public AuthController(UserAccountRepository users, PasswordEncoder encoder, JwtService jwt) {
    this.users = users;
    this.encoder = encoder;
    this.jwt = jwt;
  }

  record LoginReq(String email, String password) {
  }

  record AuthRes(String token, String email, Set<Role> roles) {
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginReq req) {
    var u = users.findByEmail(req.email()).orElse(null);
    if (u == null || !encoder.matches(req.password(), u.getPassword()))
      return ResponseEntity.status(401).body(Map.of("error", "invalid_credentials"));

    String token = jwt.generate(u.getEmail(), Map.of("roles", u.getRoles()), 1000L * 60 * 60 * 12); // 12h
    return ResponseEntity.ok(new AuthRes(token, u.getEmail(), u.getRoles()));
  }

  // simple registration (optionnel): crée un USER
  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody LoginReq req) {
    if (users.existsByEmail(req.email()))
      return ResponseEntity.badRequest().body(Map.of("error", "email_taken"));
    var u = new UserAccount(req.email(), encoder.encode(req.password()), Set.of(Role.USER));
    users.save(u);
    return ResponseEntity.ok().build();
  }
}
