package com.example.jwt_oauth.payload.request.auth;

import javax.validation.constraints.NotBlank;

import lombok.Builder;
import lombok.Data;

@Data
public class RefreshTokenRequest {
    
    @NotBlank
    private String refreshToken;

    public RefreshTokenRequest(){}

    @Builder
    public RefreshTokenRequest(String refreshToken){
        this.refreshToken = refreshToken;
    }
}
