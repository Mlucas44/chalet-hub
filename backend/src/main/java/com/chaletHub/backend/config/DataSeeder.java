package com.chaletHub.backend.config;

import com.chaletHub.backend.model.Role;
import com.chaletHub.backend.model.UserAccount;
import com.chaletHub.backend.repository.UserAccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class DataSeeder {
  @Bean
  CommandLineRunner seedUsers(UserAccountRepository users, PasswordEncoder enc) {
    return args -> {
      if (!users.existsByEmail("admin@chalethub.local")) {
        users.save(new UserAccount("admin@chalethub.local", enc.encode("admin123"), Set.of(Role.ADMIN)));
      }
    };
  }
}
