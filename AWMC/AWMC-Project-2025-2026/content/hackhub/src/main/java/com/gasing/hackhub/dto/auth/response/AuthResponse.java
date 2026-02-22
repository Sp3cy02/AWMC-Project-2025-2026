package com.gasing.hackhub.dto.auth.response;

import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private UserResponse user;
}
