package com.example.portfoliopulse.repository;

import com.example.portfoliopulse.model.PriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PriceHistoryRepository extends JpaRepository<PriceHistory, UUID> {
    Optional<PriceHistory> findTopByTickerSymbolOrderByRecordedAtDesc(String tickerSymbol);
}
