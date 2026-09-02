package com.example.portfoliopulse.controller;

import com.example.portfoliopulse.client.AlphaVantageClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/test/price")
public class TestPriceController {

    private final AlphaVantageClient alphaVantageClient;

    public TestPriceController(AlphaVantageClient alphaVantageClient) {
        this.alphaVantageClient = alphaVantageClient;
    }

    @GetMapping("/{ticker}")
    public ResponseEntity<Map<String, Object>> getPrice(@PathVariable String ticker) {
        long startTime = System.currentTimeMillis();
        BigDecimal price = alphaVantageClient.fetchCurrentPrice(ticker.toUpperCase());
        long endTime = System.currentTimeMillis();
        
        return ResponseEntity.ok(Map.of(
            "ticker", ticker.toUpperCase(),
            "price", price,
            "durationMs", endTime - startTime
        ));
    }
}
