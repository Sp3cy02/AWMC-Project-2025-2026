package com.gasing.hackhub.dto.auth.response;

import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String nome;
    private String cognome;
    private String email;
    // qui non mandiamo la password
}