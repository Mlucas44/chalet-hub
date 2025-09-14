package com.chalethub.backend.config;

import io.swagger.v3.oas.models.PathItem;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiGroupsConfig {

  /**
   * Public doc:
   * - /api/auth/** (register, login)
   * - /api/chalets/** but **GET only**
   * (on this group we hide POST/PUT/DELETE on chalets)
   */
  @Bean
  public GroupedOpenApi publicApi() {
    return GroupedOpenApi.builder()
        .group("public")
        .pathsToMatch("/api/auth/**", "/api/chalets/**")
        .addOpenApiCustomizer(openApi -> {
          if (openApi.getPaths() == null)
            return;
          openApi.getPaths().forEach((path, item) -> {
            if (path.startsWith("/api/chalets")) {
              // On the public group: show only GET
              item.setPost(null);
              item.setPut(null);
              item.setDelete(null);
              item.setPatch(null);
              item.setHead(null);
              item.setOptions(null);
              item.setTrace(null);
            }
          });
        })
        .build();
  }

  /**
   * Admin doc:
   * - /api/admin/**
   * - /api/bookings/** (réservations = endpoints authentifiés)
   * - /api/chalets/** mais **uniquement les opérations non-GET**
   * (on nettoie les entrées qui n’ont QUE du GET pour éviter le doublon avec
   * "public")
   */
  @Bean
  public GroupedOpenApi adminApi() {
    return GroupedOpenApi.builder()
        .group("admin")
        .pathsToMatch("/api/admin/**", "/api/bookings/**", "/api/chalets/**")
        .addOpenApiCustomizer(openApi -> {
          if (openApi.getPaths() == null)
            return;
          // Retire de l'admin les chemins chalets qui n’exposent que GET
          openApi.getPaths().entrySet().removeIf(entry -> {
            String path = entry.getKey();
            if (!path.startsWith("/api/chalets"))
              return false;
            PathItem item = entry.getValue();
            boolean hasNonGet = item.getPost() != null || item.getPut() != null ||
                item.getDelete() != null || item.getPatch() != null;
            return !hasNonGet; // supprime si c'est "GET only"
          });
        })
        .build();
  }
}
