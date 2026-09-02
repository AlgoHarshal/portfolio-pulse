package com.example.portfoliopulse.client;

import com.example.portfoliopulse.model.PriceHistory;
import com.example.portfoliopulse.repository.PriceHistoryRepository;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
public class AlphaVantageClientImpl implements AlphaVantageClient {

    private static final Logger logger = LoggerFactory.getLogger(AlphaVantageClientImpl.class);

    private final RestTemplate restTemplate;
    private final PriceHistoryRepository priceHistoryRepository;

    @Value("${alpha-vantage.api-key}")
    private String apiKey;

    @Value("${alpha-vantage.base-url}")
    private String baseUrl;

    public AlphaVantageClientImpl(PriceHistoryRepository priceHistoryRepository) {
        this.restTemplate = new RestTemplate();
        this.priceHistoryRepository = priceHistoryRepository;
    }

    @Override
    @Cacheable(value = "currentPrices", key = "#ticker")
    @RateLimiter(name = "alphaVantage", fallbackMethod = "fallbackPrice")
    public BigDecimal fetchCurrentPrice(String ticker) {
        logger.info("Fetching real-time price from Alpha Vantage for {}", ticker);

        String url = UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("function", "GLOBAL_QUOTE")
                .queryParam("symbol", ticker)
                .queryParam("apikey", apiKey)
                .toUriString();

        try {
            Map response = restTemplate.getForObject(url, Map.class);
            if (response != null && response.containsKey("Global Quote")) {
                Map<String, String> globalQuote = (Map<String, String>) response.get("Global Quote");
                if (globalQuote != null && globalQuote.containsKey("05. price")) {
                    BigDecimal price = new BigDecimal(globalQuote.get("05. price"));
                    
                    // Persist to PriceHistory
                    PriceHistory history = new PriceHistory();
                    history.setTickerSymbol(ticker);
                    history.setPrice(price);
                    history.setRecordedAt(LocalDateTime.now());
                    history.setSource("alphavantage");
                    priceHistoryRepository.save(history);
                    
                    return price;
                }
            }
            throw new RuntimeException("Invalid response format from Alpha Vantage");
        } catch (Exception e) {
            logger.error("Error fetching from Alpha Vantage: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Fallback method called automatically by Resilience4j if the rate limit is exceeded
     * or if the primary method throws an exception.
     */
    public BigDecimal fallbackPrice(String ticker, Throwable t) {
        logger.warn("Alpha Vantage fallback triggered for {}. Reason: {}", ticker, t.getMessage());
        logger.warn("This usually occurs if the portfolio has >5 distinct tickers and we exceeded the 5 calls/min limit, or the API is down.");

        Optional<PriceHistory> lastKnownPrice = priceHistoryRepository.findTopByTickerSymbolOrderByRecordedAtDesc(ticker);
        
        if (lastKnownPrice.isPresent()) {
            logger.info("Using cached/stale price from database for {}: {}", ticker, lastKnownPrice.get().getPrice());
            return lastKnownPrice.get().getPrice();
        }

        logger.error("No historical price found in database for {}. Returning 0 as absolute fallback.", ticker);
        return BigDecimal.ZERO;
    }
}
