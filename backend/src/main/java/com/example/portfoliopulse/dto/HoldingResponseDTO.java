package com.example.portfoliopulse.dto;

import com.example.portfoliopulse.model.AssetType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class HoldingResponseDTO {
    private UUID id;
    private String tickerSymbol;
    private AssetType assetType;
    private BigDecimal quantity;
    private BigDecimal purchasePrice;
    private LocalDateTime purchaseDate;
    private String sector;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
