package com.example.jwt_oauth.payload.request.auth;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import lombok.Data;
import lombok.Getter;

@Data
// @Getter
public class SignInRequest {
    
    // import io.swagger.v3.oas.annotations.media.Schema;
    // @Schema()
    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;
}
