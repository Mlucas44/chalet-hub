package com.chaletHub.backend.controller;

import com.chaletHub.backend.model.Booking;
import com.chaletHub.backend.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "http://localhost:5173")
@Tag(name = "Bookings", description = "Réservations")
@RequiredArgsConstructor
public class BookingController {

  private final BookingService service;

  // DTO d'entrée pour POST
  public record BookingReq(Long chaletId, LocalDate startDate, LocalDate endDate) {
  }

  @Operation(summary = "Lister toutes les réservations")
  @ApiResponse(responseCode = "200", description = "OK")
  @GetMapping
  public List<Booking> listAll() {
    return service.listAll();
  }

  @Operation(summary = "Lister les réservations d’un chalet")
  @ApiResponse(responseCode = "200", description = "OK")
  @GetMapping("/chalet/{chaletId}")
  public List<Booking> listByChalet(@PathVariable Long chaletId) {
    return service.listByChalet(chaletId);
  }

  @Operation(summary = "Créer une réservation", description = "Calcule nuits + taxes (TPS/TVQ) et refuse les chevauchements.")
  @ApiResponse(responseCode = "200", description = "Réservation créée")
  @ApiResponse(responseCode = "400", description = "Dates invalides")
  @ApiResponse(responseCode = "409", description = "Conflit de réservation")
  @PostMapping
  public ResponseEntity<?> create(@RequestBody BookingReq req) {
    try {
      Booking created = service.create(req.chaletId(), req.startDate(), req.endDate());
      return ResponseEntity.ok(created);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (IllegalStateException e) {
      return ResponseEntity.status(409).body(e.getMessage());
    }
  }

  @Operation(summary = "Annuler une réservation")
  @ApiResponse(responseCode = "204", description = "Supprimée")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> cancel(@PathVariable Long id) {
    service.cancel(id);
    return ResponseEntity.noContent().build();
  }
}
