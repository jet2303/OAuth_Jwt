package com.example.jwt_oauth.payload.request.auth;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;

@Data
@Builder
public class ChangePasswordRequest {

    @NotBlank
    private String email;

    @NotBlank
    private String oldPassword;

    @NotBlank
    private String newPassword;

    @NotBlank
    private String chkNewPassword;
    
}
