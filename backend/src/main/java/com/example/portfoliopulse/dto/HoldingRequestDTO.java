package com.example.portfoliopulse.dto;

import com.example.portfoliopulse.model.AssetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class HoldingRequestDTO {
    @NotBlank(message = "Ticker symbol is required")
    private String tickerSymbol;

    @NotNull(message = "Asset type is required")
    private AssetType assetType;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be greater than zero")
    private BigDecimal quantity;

    @NotNull(message = "Purchase price is required")
    @Positive(message = "Purchase price must be greater than zero")
    private BigDecimal purchasePrice;

    @NotNull(message = "Purchase date is required")
    @PastOrPresent(message = "Purchase date cannot be in the future")
    private LocalDateTime purchaseDate;
    
    private String sector;
}
