package com.chaletHub.backend.controller;

import com.chaletHub.backend.model.Chalet;
import com.chaletHub.backend.repository.ChaletRepository;
import com.chaletHub.backend.repository.ChaletSpecs;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/chalets")
@CrossOrigin(origins = "http://localhost:5173")
@Tag(name = "Chalets", description = "Catalogue public des chalets")
public class ChaletController {

  private final ChaletRepository repo;

  public ChaletController(ChaletRepository repo) {
    this.repo = repo;
  }

  @Operation(summary = "Lister les chalets", description = "Retourne la liste de tous les chalets disponibles. "
      + "Plus tard, on pourra ajouter des filtres (prix, région, disponibilité).")
  @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
  @GetMapping
  public List<Chalet> getAll() {
    return repo.findAll();
  }

  @Operation(summary = "Détail d’un chalet", description = "Récupère un chalet par son identifiant.")
  @ApiResponse(responseCode = "200", description = "Trouvé")
  @ApiResponse(responseCode = "404", description = "Non trouvé")
  @GetMapping("/{id}")
  public ResponseEntity<Chalet> getOne(@PathVariable Long id) {
    return repo.findById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @Operation(summary = "Créer un nouveau chalet", description = "Ajoute un chalet dans la base. "
      + "Cette route est ouverte pour le moment (dev), mais sera réservée aux admins/hosts.")
  @ApiResponse(responseCode = "200", description = "Chalet créé avec succès")
  @ApiResponse(responseCode = "400", description = "Requête invalide (champs manquants ou incorrects)")
  @PostMapping
  public Chalet addChalet(@RequestBody Chalet chalet) {
    return repo.save(chalet);
  }

  // --- Recherche paginée/tri/filtre ---
  private static final Set<String> ALLOWED_SORT = Set.of("id", "name", "pricePerNight");

  @Operation(summary = "Recherche paginée avec filtres")
  @ApiResponse(responseCode = "200")
  @GetMapping("/search")
  public Page<Chalet> search(
      @RequestParam(required = false) String q,
      @RequestParam(required = false) String city,
      @RequestParam(required = false) BigDecimal minPrice,
      @RequestParam(required = false) BigDecimal maxPrice,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "id,desc") String sort) {
    // sort="field,dir"
    String[] parts = sort.split(",", 2);
    String field = parts[0].trim();
    String dir = parts.length > 1 ? parts[1].trim() : "asc";

    if (!ALLOWED_SORT.contains(field)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Invalid sort field. Allowed: " + ALLOWED_SORT);
    }

    Sort.Direction direction = "desc".equalsIgnoreCase(dir) ? Sort.Direction.DESC : Sort.Direction.ASC;
    Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), Sort.by(direction, field));

    Specification<Chalet> spec = Specification
        .where(ChaletSpecs.textSearch(q))
        .and(ChaletSpecs.locationLike(city))
        .and(ChaletSpecs.priceMin(minPrice))
        .and(ChaletSpecs.priceMax(maxPrice));

    return repo.findAll(spec, pageable);
  }

}
