package com.chaletHub.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "bookings")
@Getter
@Setter
public class Booking {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "chalet_id")
  private Chalet chalet;

  private LocalDate startDate; // date d'arrivée
  private LocalDate endDate; // date de départ (exclusive)

  private Integer nights;

  private BigDecimal subtotal; // nights * pricePerNight
  private BigDecimal tps; // 5%
  private BigDecimal tvq; // 9.975%
  private BigDecimal total; // subtotal + tps + tvq

  @Version
  private Long version; // verrouillage optimiste
}
