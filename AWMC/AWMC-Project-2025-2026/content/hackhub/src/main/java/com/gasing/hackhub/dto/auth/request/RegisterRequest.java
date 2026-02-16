package com.gasing.hackhub.dto.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// Corrisponde a RegisterRequest nel diagramma
@Data
public class RegisterRequest {
    @NotBlank
    private String nome;
    @NotBlank
    private String cognome;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String password;
}