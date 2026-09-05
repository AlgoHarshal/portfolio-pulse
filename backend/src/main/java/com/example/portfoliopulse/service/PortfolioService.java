package com.example.portfoliopulse.service;

import com.example.portfoliopulse.client.AlphaVantageClient;
import com.example.portfoliopulse.dto.HoldingSummaryDTO;
import com.example.portfoliopulse.dto.PortfolioSummaryDTO;
import com.example.portfoliopulse.model.Holding;
import com.example.portfoliopulse.model.User;
import com.example.portfoliopulse.repository.HoldingRepository;
import com.example.portfoliopulse.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PortfolioService {

    private final HoldingRepository holdingRepository;
    private final UserRepository userRepository;
    private final AlphaVantageClient alphaVantageClient;

    public PortfolioService(HoldingRepository holdingRepository, UserRepository userRepository, AlphaVantageClient alphaVantageClient) {
        this.holdingRepository = holdingRepository;
        this.userRepository = userRepository;
        this.alphaVantageClient = alphaVantageClient;
    }

    public PortfolioSummaryDTO getPortfolioSummary(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Holding> holdings = holdingRepository.findAllByUserId(user.getId());

        // Deduplicate tickers to ensure we only make 1 rate-limited call per unique ticker
        Map<String, BigDecimal> livePrices = holdings.stream()
                .map(Holding::getTickerSymbol)
                .distinct()
                .collect(Collectors.toMap(
                        ticker -> ticker,
                        alphaVantageClient::fetchCurrentPrice
                ));

        BigDecimal totalCurrentValue = BigDecimal.ZERO;
        BigDecimal totalCostBasis = BigDecimal.ZERO;
        boolean hasMissingPrices = false;

        List<HoldingSummaryDTO> holdingSummaries = holdings.stream().map(holding -> {
            BigDecimal quantity = holding.getQuantity();
            BigDecimal purchasePrice = holding.getPurchasePrice();
            BigDecimal holdingCostBasis = purchasePrice.multiply(quantity).setScale(2, RoundingMode.HALF_UP);
            
            BigDecimal currentPrice = livePrices.get(holding.getTickerSymbol());
            boolean priceAvailable = currentPrice != null;

            HoldingSummaryDTO.HoldingSummaryDTOBuilder builder = HoldingSummaryDTO.builder()
                    .id(holding.getId())
                    .tickerSymbol(holding.getTickerSymbol())
                    .assetType(holding.getAssetType())
                    .quantity(quantity)
                    .purchasePrice(purchasePrice)
                    .purchaseDate(holding.getPurchaseDate())
                    .sector(holding.getSector())
                    .totalCostBasis(holdingCostBasis)
                    .priceAvailable(priceAvailable);

            if (priceAvailable) {
                BigDecimal currentValue = currentPrice.multiply(quantity).setScale(2, RoundingMode.HALF_UP);
                BigDecimal absoluteGain = currentValue.subtract(holdingCostBasis);
                BigDecimal percentageGain = calculatePercentage(absoluteGain, holdingCostBasis);

                builder.currentPrice(currentPrice)
                       .currentValue(currentValue)
                       .absoluteGain(absoluteGain)
                       .percentageGain(percentageGain);
            }

            return builder.build();
        }).collect(Collectors.toList());

        // Aggregate Totals - Exclude any holding where priceAvailable=false to keep math accurate
        for (HoldingSummaryDTO summary : holdingSummaries) {
            if (summary.isPriceAvailable()) {
                totalCurrentValue = totalCurrentValue.add(summary.getCurrentValue());
                totalCostBasis = totalCostBasis.add(summary.getTotalCostBasis());
            } else {
                hasMissingPrices = true;
            }
        }

        BigDecimal totalAbsoluteGain = totalCurrentValue.subtract(totalCostBasis);
        BigDecimal totalPercentageGain = calculatePercentage(totalAbsoluteGain, totalCostBasis);

        String warningMessage = hasMissingPrices 
                ? "Some holdings are missing current pricing due to rate limits. They have been excluded from the total portfolio valuation." 
                : null;

        return PortfolioSummaryDTO.builder()
                .totalCurrentValue(totalCurrentValue)
                .totalCostBasis(totalCostBasis)
                .totalAbsoluteGain(totalAbsoluteGain)
                .totalPercentageGain(totalPercentageGain)
                .holdings(holdingSummaries)
                .warningMessage(warningMessage)
                .build();
    }

    private BigDecimal calculatePercentage(BigDecimal absoluteGain, BigDecimal costBasis) {
        if (costBasis == null || costBasis.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO; // Guard against divide-by-zero
        }
        return absoluteGain.divide(costBasis, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
