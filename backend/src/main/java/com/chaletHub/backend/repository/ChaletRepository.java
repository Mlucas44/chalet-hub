package com.chaletHub.backend.repository;

import com.chaletHub.backend.model.Chalet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChaletRepository extends JpaRepository<Chalet, Long> {
}
