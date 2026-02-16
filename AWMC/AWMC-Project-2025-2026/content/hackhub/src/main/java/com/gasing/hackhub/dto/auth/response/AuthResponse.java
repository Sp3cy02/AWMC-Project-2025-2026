package com.gasing.hackhub.dto.auth.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    
    private String token;          // Il pass per Spring Security
    
    private UserResponse userData; // I dati pubblici dell'utente (nome, cognome, email)
    
}