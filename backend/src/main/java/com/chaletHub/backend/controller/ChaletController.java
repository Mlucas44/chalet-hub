package com.chaletHub.backend.controller;

import com.chaletHub.backend.model.Chalet;
import com.chaletHub.backend.repository.ChaletRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

  @Operation(summary = "Créer un nouveau chalet", description = "Ajoute un chalet dans la base. "
      + "Cette route est ouverte pour le moment (dev), mais sera réservée aux admins/hosts.")
  @ApiResponse(responseCode = "200", description = "Chalet créé avec succès")
  @ApiResponse(responseCode = "400", description = "Requête invalide (champs manquants ou incorrects)")
  @PostMapping
  public Chalet addChalet(@RequestBody Chalet chalet) {
    return repo.save(chalet);
  }
}
