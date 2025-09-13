package com.chaletHub.backend.repository;

import com.chaletHub.backend.model.Chalet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ChaletRepository extends JpaRepository<Chalet, Long>, JpaSpecificationExecutor<Chalet> {

  @Query("""
        select c from Chalet c
        where (:location is null or lower(c.location) like lower(concat('%', :location, '%')))
          and (:minPrice is null or c.pricePerNight >= :minPrice)
          and (:maxPrice is null or c.pricePerNight <= :maxPrice)
      """)
  Page<Chalet> search(@Param("location") String location,
      @Param("minPrice") Double minPrice,
      @Param("maxPrice") Double maxPrice,
      Pageable pageable);
}
