package com.example.portfoliopulse.controller;

import com.example.portfoliopulse.dto.HoldingRequestDTO;
import com.example.portfoliopulse.dto.HoldingResponseDTO;
import com.example.portfoliopulse.service.HoldingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/holdings")
public class HoldingController {

    private final HoldingService holdingService;

    public HoldingController(HoldingService holdingService) {
        this.holdingService = holdingService;
    }

    @GetMapping
    public ResponseEntity<List<HoldingResponseDTO>> getAllHoldings() {
        return ResponseEntity.ok(holdingService.getAllHoldings());
    }

    @PostMapping
    public ResponseEntity<HoldingResponseDTO> createHolding(@Valid @RequestBody HoldingRequestDTO request) {
        HoldingResponseDTO response = holdingService.createHolding(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HoldingResponseDTO> updateHolding(
            @PathVariable UUID id, 
            @Valid @RequestBody HoldingRequestDTO request) {
        HoldingResponseDTO response = holdingService.updateHolding(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHolding(@PathVariable UUID id) {
        holdingService.deleteHolding(id);
        return ResponseEntity.noContent().build();
    }
}
