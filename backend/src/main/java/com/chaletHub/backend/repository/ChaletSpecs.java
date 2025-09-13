package com.chaletHub.backend.repository;

import com.chaletHub.backend.model.Chalet;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public final class ChaletSpecs {
  private ChaletSpecs() {
  }

  /** Global search: matches name OR location (contains, case-insensitive) */
  public static Specification<Chalet> textSearch(String q) {
    if (q == null || q.isBlank())
      return null;
    String p = "%" + q.toLowerCase() + "%";
    return (root, cq, cb) -> cb.or(
        cb.like(cb.lower(root.get("name")), p),
        cb.like(cb.lower(root.get("location")), p));
  }

  /** City/region filter: contains on 'location' (case-insensitive) */
  public static Specification<Chalet> locationLike(String city) {
    if (city == null || city.isBlank())
      return null;
    String p = "%" + city.toLowerCase() + "%";
    return (root, cq, cb) -> cb.like(cb.lower(root.get("location")), p);
  }

  public static Specification<Chalet> priceMin(BigDecimal min) {
    if (min == null)
      return null;
    return (root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("pricePerNight"), min);
  }

  public static Specification<Chalet> priceMax(BigDecimal max) {
    if (max == null)
      return null;
    return (root, cq, cb) -> cb.lessThanOrEqualTo(root.get("pricePerNight"), max);
  }
}
