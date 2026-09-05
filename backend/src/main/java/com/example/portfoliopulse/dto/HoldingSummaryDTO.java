package com.example.portfoliopulse.dto;

import com.example.portfoliopulse.model.AssetType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class HoldingSummaryDTO {
    private UUID id;
    private String tickerSymbol;
    private AssetType assetType;
    private BigDecimal quantity;
    private BigDecimal purchasePrice;
    private LocalDateTime purchaseDate;
    private String sector;

    // Live calculations
    private boolean priceAvailable;
    private BigDecimal currentPrice;
    private BigDecimal currentValue;
    private BigDecimal totalCostBasis;
    private BigDecimal absoluteGain;
    private BigDecimal percentageGain;
}
