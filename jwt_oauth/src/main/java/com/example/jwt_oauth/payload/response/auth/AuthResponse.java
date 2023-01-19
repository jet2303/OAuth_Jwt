package com.example.jwt_oauth.payload.response.auth;

import lombok.Builder;
import lombok.Data;

@Data
public class AuthResponse {
    
    private String accessToken;

    private String refreshToken;

    private String tokenType = "Bearer";

    public AuthResponse(){};

    @Builder
    public AuthResponse(String accessToken, String refreshToken){
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
