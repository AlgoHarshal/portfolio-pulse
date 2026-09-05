package com.example.portfoliopulse.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class PortfolioSummaryDTO {
    private BigDecimal totalCurrentValue;
    private BigDecimal totalCostBasis;
    private BigDecimal totalAbsoluteGain;
    private BigDecimal totalPercentageGain;
    
    private String warningMessage;
    
    private List<HoldingSummaryDTO> holdings;
}
