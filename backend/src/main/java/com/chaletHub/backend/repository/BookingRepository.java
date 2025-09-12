package com.chaletHub.backend.repository;

import com.chaletHub.backend.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

  // liste des réservations d’un chalet
  List<Booking> findByChaletId(Long chaletId);

  // détection de chevauchement
  @Query("""
        select count(b) > 0 from Booking b
        where b.chalet.id = :chaletId
          and b.startDate < :endDate
          and b.endDate   > :startDate
      """)
  boolean overlaps(@Param("chaletId") Long chaletId,
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate);
}
