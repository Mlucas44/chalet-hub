package com.chaletHub.backend.service;

import com.chaletHub.backend.model.Booking;
import com.chaletHub.backend.model.Chalet;
import com.chaletHub.backend.repository.BookingRepository;
import com.chaletHub.backend.repository.ChaletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

  private final ChaletRepository chalets;
  private final BookingRepository bookings;

  /** === demandée par le contrôleur === */
  public List<Booking> listAll() {
    return bookings.findAll();
  }

  /** === demandée par le contrôleur === */
  public List<Booking> listByChalet(Long chaletId) {
    return bookings.findByChaletId(chaletId);
  }

  /** création (utilisée par le POST du contrôleur) */
  public Booking create(Long chaletId, LocalDate start, LocalDate end) {
    // 1) Validation dates
    if (start == null || end == null || !start.isBefore(end)) {
      throw new IllegalArgumentException("Dates invalides");
    }
    // 2) Conflit
    if (bookings.overlaps(chaletId, start, end)) {
      throw new IllegalStateException("Conflit de réservation");
    }
    // 3) Calculs
    Chalet c = chalets.findById(chaletId).orElseThrow();
    int nights = (int) ChronoUnit.DAYS.between(start, end);
    if (nights <= 0)
      throw new IllegalArgumentException("Au moins 1 nuit");

    BigDecimal price = BigDecimal.valueOf(c.getPricePerNight());
    BigDecimal subtotal = price.multiply(BigDecimal.valueOf(nights));
    BigDecimal tps = subtotal.multiply(new BigDecimal("0.05"));
    BigDecimal tvq = subtotal.multiply(new BigDecimal("0.09975"));
    BigDecimal total = subtotal.add(tps).add(tvq);

    // 4) Persistance
    Booking b = new Booking();
    b.setChalet(c);
    b.setStartDate(start);
    b.setEndDate(end);
    b.setNights(nights);
    b.setSubtotal(subtotal);
    b.setTps(tps);
    b.setTvq(tvq);
    b.setTotal(total);

    return bookings.save(b);
  }

  /** === demandée par le contrôleur === */
  public void cancel(Long bookingId) {
    bookings.deleteById(bookingId);
  }
}
