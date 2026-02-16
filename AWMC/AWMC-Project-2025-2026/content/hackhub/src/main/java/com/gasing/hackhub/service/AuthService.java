package com.gasing.hackhub.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gasing.hackhub.config.JwtService;
import com.gasing.hackhub.dto.auth.request.LoginRequest;
import com.gasing.hackhub.dto.auth.request.RegisterRequest;
import com.gasing.hackhub.dto.auth.response.AuthResponse;
import com.gasing.hackhub.dto.auth.response.UserResponse;
import com.gasing.hackhub.model.User;
import com.gasing.hackhub.repository.UserRepository;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private AuthenticationManager authenticationManager;

    // --- 1. REGISTRAZIONE ---
    // NOTA BENE: Il tipo di ritorno ora è AuthResponse!
    public AuthResponse register(RegisterRequest request) {
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email già in uso!");
        }

        User user = new User();
        user.setNome(request.getNome());
        user.setCognome(request.getCognome());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);

        // Genero il Token e i dati utente
        String jwtToken = jwtService.generateToken(savedUser);
        UserResponse userData = mapToUserResponse(savedUser);

        // Restituisco la scatola grande che contiene ENTRAMBI
        return new AuthResponse(jwtToken, userData);
    }

    // --- 2. LOGIN ---
    // NOTA BENE: Il tipo di ritorno ora è AuthResponse!
    public AuthResponse login(LoginRequest request) {
        
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        // Genero il Token e i dati utente
        String jwtToken = jwtService.generateToken(user);
        UserResponse userData = mapToUserResponse(user);

        // Restituisco la scatola grande che contiene ENTRAMBI
        return new AuthResponse(jwtToken, userData);
    }

    // --- METODO PRIVATO ---
    // Questo continua a restituire UserResponse, ma viene usato solo qui dentro
    private UserResponse mapToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setNome(user.getNome());
        response.setCognome(user.getCognome());
        response.setEmail(user.getEmail());
        return response;
    }
}