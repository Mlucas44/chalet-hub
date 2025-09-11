package com.chalethub.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.ExternalDocumentation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI chaletHubOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("ChaletHub API")
            .version("v1")
            .description("API publique et admin de ChaletHub (catalogue, réservations, etc.)")
            .contact(new Contact().name("Lucas").email("dev@chalethub.local")))
        .externalDocs(new ExternalDocumentation()
            .description("README / Notes")
            .url("https://example.com/chalethub")); // optionnel
  }
}
