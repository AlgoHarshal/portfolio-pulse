package com.example.portfoliopulse.service;

import com.example.portfoliopulse.dto.HoldingRequestDTO;
import com.example.portfoliopulse.dto.HoldingResponseDTO;
import com.example.portfoliopulse.exception.ResourceNotFoundException;
import com.example.portfoliopulse.model.Holding;
import com.example.portfoliopulse.model.User;
import com.example.portfoliopulse.repository.HoldingRepository;
import com.example.portfoliopulse.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class HoldingService {

    private final HoldingRepository holdingRepository;
    private final UserRepository userRepository;

    public HoldingService(HoldingRepository holdingRepository, UserRepository userRepository) {
        this.holdingRepository = holdingRepository;
        this.userRepository = userRepository;
    }

    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email;
        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else {
            email = principal.toString();
        }
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Current authenticated user not found in database"));
    }

    public HoldingResponseDTO createHolding(HoldingRequestDTO request) {
        User currentUser = getCurrentUser();
        Holding holding = new Holding();
        holding.setUser(currentUser);
        // Ensure ticker is uppercase
        holding.setTickerSymbol(request.getTickerSymbol().trim().toUpperCase());
        holding.setAssetType(request.getAssetType());
        holding.setQuantity(request.getQuantity());
        holding.setPurchasePrice(request.getPurchasePrice());
        holding.setPurchaseDate(request.getPurchaseDate());
        holding.setSector(request.getSector());

        Holding savedHolding = holdingRepository.save(holding);
        return mapToDTO(savedHolding);
    }

    public List<HoldingResponseDTO> getAllHoldings() {
        User currentUser = getCurrentUser();
        return holdingRepository.findAllByUserId(currentUser.getId())
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public HoldingResponseDTO updateHolding(UUID id, HoldingRequestDTO request) {
        User currentUser = getCurrentUser();
        Holding holding = holdingRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Holding not found or you don't have permission to access it"));

        // Ensure ticker is uppercase
        holding.setTickerSymbol(request.getTickerSymbol().trim().toUpperCase());
        holding.setAssetType(request.getAssetType());
        holding.setQuantity(request.getQuantity());
        holding.setPurchasePrice(request.getPurchasePrice());
        holding.setPurchaseDate(request.getPurchaseDate());
        holding.setSector(request.getSector());

        Holding updatedHolding = holdingRepository.save(holding);
        return mapToDTO(updatedHolding);
    }

    public void deleteHolding(UUID id) {
        User currentUser = getCurrentUser();
        Holding holding = holdingRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Holding not found or you don't have permission to access it"));
        
        holdingRepository.delete(holding);
    }

    private HoldingResponseDTO mapToDTO(Holding holding) {
        HoldingResponseDTO dto = new HoldingResponseDTO();
        dto.setId(holding.getId());
        dto.setTickerSymbol(holding.getTickerSymbol());
        dto.setAssetType(holding.getAssetType());
        dto.setQuantity(holding.getQuantity());
        dto.setPurchasePrice(holding.getPurchasePrice());
        dto.setPurchaseDate(holding.getPurchaseDate());
        dto.setSector(holding.getSector());
        dto.setCreatedAt(holding.getCreatedAt());
        dto.setUpdatedAt(holding.getUpdatedAt());
        return dto;
    }
}
