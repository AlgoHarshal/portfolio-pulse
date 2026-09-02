package com.example.portfoliopulse.client;

import java.math.BigDecimal;

public interface AlphaVantageClient {
    BigDecimal fetchCurrentPrice(String ticker);
}
