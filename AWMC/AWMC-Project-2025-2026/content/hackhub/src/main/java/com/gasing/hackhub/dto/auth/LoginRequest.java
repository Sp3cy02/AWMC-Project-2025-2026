package com.gasing.hackhub.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// Corrisponde a LoginRequest nel diagramma
public record LoginRequest(
    @NotBlank @Email String email,
    @NotBlank String password
) {}