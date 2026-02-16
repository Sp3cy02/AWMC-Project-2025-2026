package com.gasing.hackhub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.gasing.hackhub.dto.auth.request.LoginRequest;
import com.gasing.hackhub.dto.auth.request.RegisterRequest;
import com.gasing.hackhub.dto.auth.response.AuthResponse;
import com.gasing.hackhub.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // --- REGISTRAZIONE ---
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    // --- LOGIN ---
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}