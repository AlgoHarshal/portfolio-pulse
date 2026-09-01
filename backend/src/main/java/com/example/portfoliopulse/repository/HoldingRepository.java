package com.example.portfoliopulse.repository;

import com.example.portfoliopulse.model.Holding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface HoldingRepository extends JpaRepository<Holding, UUID> {
    List<Holding> findAllByUserId(UUID userId);
    Optional<Holding> findByIdAndUserId(UUID id, UUID userId);
}
