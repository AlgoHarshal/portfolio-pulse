package com.example.portfoliopulse.controller;

import com.example.portfoliopulse.dto.PortfolioSummaryDTO;
import com.example.portfoliopulse.service.PortfolioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping("/summary")
    public ResponseEntity<PortfolioSummaryDTO> getSummary(Authentication authentication) {
        String email = authentication.getName();
        PortfolioSummaryDTO summary = portfolioService.getPortfolioSummary(email);
        return ResponseEntity.ok(summary);
    }
}
