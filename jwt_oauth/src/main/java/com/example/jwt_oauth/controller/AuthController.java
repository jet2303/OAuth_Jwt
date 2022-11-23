package com.example.jwt_oauth.controller;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.jwt_oauth.config.security.token.CurrentUser;
import com.example.jwt_oauth.config.security.token.UserPrincipal;
import com.example.jwt_oauth.payload.request.auth.ChangePasswordRequest;
import com.example.jwt_oauth.payload.request.auth.RefreshTokenRequest;
import com.example.jwt_oauth.payload.request.auth.SignInRequest;
import com.example.jwt_oauth.payload.request.auth.SignUpRequest;
import com.example.jwt_oauth.service.user.auth.AuthService;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Controller
// @RestController
@RequestMapping(value = "/auth")
public class AuthController {
    
    
    private final AuthService authService;

    
    @GetMapping(value = "/")
    public ResponseEntity<?> whoAmI(@CurrentUser UserPrincipal userPrincipal) {
        return authService.whoAmI(userPrincipal);
    }

    
    @DeleteMapping(value = "/")
    public ResponseEntity<?> delete(@CurrentUser UserPrincipal userPrincipal){
        return authService.delete(userPrincipal);
    }


    @PutMapping(value = "/")
    public ResponseEntity<?> modify(@CurrentUser UserPrincipal userPrincipal, 
                                    @Valid @RequestBody ChangePasswordRequest passwordChangeRequest){
        return authService.modify(userPrincipal, passwordChangeRequest);
    }

    @PostMapping(value = "/signin")
    public ResponseEntity<?> signin(@Valid @RequestBody SignInRequest signInRequest) {
        return authService.signin(signInRequest);
    }

    
    @PostMapping(value = "/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignUpRequest signUpRequest) {
        return authService.signup(signUpRequest);
    }

    
    @PostMapping(value = "/refresh")
    public ResponseEntity<?> refresh(@Valid @RequestBody RefreshTokenRequest tokenRefreshRequest) {
        return authService.refresh(tokenRefreshRequest);
    }


    @PostMapping(value="/signout")
    public ResponseEntity<?> signout(@CurrentUser UserPrincipal userPrincipal, 
                                        @Valid @RequestBody RefreshTokenRequest tokenRefreshRequest) {
        return authService.signout(tokenRefreshRequest);
    }

    @GetMapping(value = "/loginPage")
    public String loginPage(){
        return "login";
    }

}
