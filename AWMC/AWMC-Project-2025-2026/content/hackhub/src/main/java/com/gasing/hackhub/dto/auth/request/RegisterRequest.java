package com.gasing.hackhub.dto.auth.request;

import lombok.Data;

@Data
public class RegisterRequest {
    private String nome;
    private String cognome;
    private String email;
    private String password; // La password in chiaro che l'utente digita
}