package com.gasing.hackhub.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// Corrisponde a RegisterRequest nel diagramma
public record RegisterRequest(
    @NotBlank String nome,
    @NotBlank String cognome,
    @NotBlank @Email String email,
    @NotBlank String password
) {}