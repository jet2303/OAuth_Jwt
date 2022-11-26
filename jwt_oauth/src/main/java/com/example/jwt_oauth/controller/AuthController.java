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
import org.springframework.web.servlet.ModelAndView;

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
@RequestMapping(value = "/auth")
public class AuthController {
    
    
    private final AuthService authService;

//     
//  파라미터의 @Valid @RequestBody는 Thymeleaf 템플릿 엔진이 보내는 Request의 content Type과 호환되지않아 삭제함.
//     
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
    // public ResponseEntity<?> signin(@Valid @RequestBody SignInRequest signInRequest) {
    public ResponseEntity<?> signin(SignInRequest signInRequest) {
        return authService.signin(signInRequest);
    }

    
    // @PostMapping(value = "/signup")
    // // public ResponseEntity<?> signup(@Valid @RequestBody SignUpRequest signUpRequest) {
    // public ResponseEntity<?> signup(SignUpRequest signUpRequest) {
    //     return authService.signup(signUpRequest);
    // }

    @PostMapping(value = "/signup")
    // public ResponseEntity<?> signup(@Valid @RequestBody SignUpRequest signUpRequest) {
    public String signup(SignUpRequest signUpRequest) {
        authService.signup(signUpRequest);
        //회원가입후 redirect 하기위해 String으로 redirect
        return "redirect:/auth/loginPage"; 
    }
    

    
    @PostMapping(value = "/refresh")
    // public ResponseEntity<?> refresh(@Valid @RequestBody RefreshTokenRequest tokenRefreshRequest) {
    public ResponseEntity<?> refresh(RefreshTokenRequest tokenRefreshRequest) {
        return authService.refresh(tokenRefreshRequest);
    }


    @PostMapping(value="/signout")
    // public ResponseEntity<?> signout(@CurrentUser UserPrincipal userPrincipal, 
    //                                     @Valid @RequestBody RefreshTokenRequest tokenRefreshRequest) {
    public ResponseEntity<?> signout(@CurrentUser UserPrincipal userPrincipal, 
                                        RefreshTokenRequest tokenRefreshRequest) {
        return authService.signout(tokenRefreshRequest);
    }

    @GetMapping(value = "/loginPage")
    public String loginPage(){
        return "login";
    }

    @GetMapping(value = "/home")
    public String home(){
        return "home";
    }

    @GetMapping(value="/customSignup")
    public String customSignup(ModelAndView modelAndView){
        modelAndView.addObject("SignUpRequest", new SignUpRequest());
        return "customSignup";
    }

    @GetMapping(value="/signup")
    public String signup(){
        return "signup";
    }

    @GetMapping(value = "/main")
    public String main(){
        return "main";
    }

}
