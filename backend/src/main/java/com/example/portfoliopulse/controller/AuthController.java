package com.example.portfoliopulse.controller;

import com.example.portfoliopulse.dto.AuthResponseDTO;
import com.example.portfoliopulse.dto.LoginRequestDTO;
import com.example.portfoliopulse.dto.SignupRequestDTO;
import com.example.portfoliopulse.model.User;
import com.example.portfoliopulse.repository.UserRepository;
import com.example.portfoliopulse.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository,
                          PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequestDTO signUpRequest) {
        if (userRepository.findByEmail(signUpRequest.getEmail()).isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Error: Email is already in use!"));
        }

        User user = new User();
        user.setEmail(signUpRequest.getEmail());
        user.setPasswordHash(passwordEncoder.encode(signUpRequest.getPassword()));

        userRepository.save(user);

        String jwt = jwtUtil.generateToken(user.getEmail(), user.getId().toString());
        return ResponseEntity.ok(new AuthResponseDTO(jwt, user.getEmail(), user.getId()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequestDTO loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            User user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String jwt = jwtUtil.generateToken(user.getEmail(), user.getId().toString());
            return ResponseEntity.ok(new AuthResponseDTO(jwt, user.getEmail(), user.getId()));

        } catch (AuthenticationException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Error: Unauthorized - Invalid credentials"));
        }
    }
}
