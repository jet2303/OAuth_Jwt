package com.example.jwt_oauth.controller;

import java.lang.ProcessBuilder.Redirect;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import com.example.jwt_oauth.payload.response.AuthResponse;
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
    // public ResponseEntity<?> whoAmI(UserPrincipal userPrincipal) {
        return authService.whoAmI(userPrincipal);
    }

    
    @DeleteMapping(value = "/")
    // public ResponseEntity<?> delete(@CurrentUser UserPrincipal userPrincipal){
    public ResponseEntity<?> delete(@CurrentUser UserPrincipal userPrincipal){
        return authService.delete(userPrincipal);
    }


    @PutMapping(value = "/")
    // public ResponseEntity<?> modify(@CurrentUser UserPrincipal userPrincipal, 
    //                                 @Valid @RequestBody ChangePasswordRequest passwordChangeRequest){
    public ResponseEntity<?> modify(@RequestBody ChangePasswordRequest passwordChangeRequest, @CurrentUser UserPrincipal userPrincipal){
        return authService.modify(userPrincipal, passwordChangeRequest);
    }

    @PostMapping(value = "/signin")
    // public ResponseEntity<?> signin(@Valid @RequestBody SignInRequest signInRequest) {
    public ResponseEntity<?> signin(SignInRequest signInRequest, HttpServletResponse response) {
        return authService.signin(signInRequest, response);
    }
    
    // @PostMapping(value = "/signup")
    // // public ResponseEntity<?> signup(@Valid @RequestBody SignUpRequest signUpRequest) {
    // public ResponseEntity<?> signup(SignUpRequest signUpRequest) {
    //     return authService.signup(signUpRequest);
    // }

    @PostMapping(value = "/signup")
    // public ResponseEntity<?> signup(@Valid @RequestBody SignUpRequest signUpRequest) {
    public ResponseEntity<?> signup(SignUpRequest signUpRequest, HttpServletResponse response, HttpServletRequest request) {

        return authService.signup(signUpRequest, response);
        // if(response.getStatus()>=300 || response.getStatus()<200){
        //     return "redirect:/auth/customSignup";
        // }
        // //회원가입후 redirect 하기위해 String으로 redirect
        // return "redirect:/auth/loginPage"; 
    }
    

    
    @PostMapping(value = "/refresh")
    // public ResponseEntity<?> refresh(@Valid @RequestBody RefreshTokenRequest tokenRefreshRequest) {
    public ResponseEntity<?> refresh(RefreshTokenRequest tokenRefreshRequest) {
        return authService.refresh(tokenRefreshRequest);
    }

    //일단 get으로 logout 할수 있게 만들어놓음.
    @GetMapping(value="/signout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response
                            ,@CurrentUser UserPrincipal userPrincipal){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication != null){
            return authService.signout(authentication);
        }
        return authService.signout(authentication);
        
    }

    // @PostMapping(value="/signout")
    // // public ResponseEntity<?> signout(@CurrentUser UserPrincipal userPrincipal
    // //                                     ,@Valid @RequestBody RefreshTokenRequest tokenRefreshRequest) {
    // public String signout(Authentication authentication) {
    //     authService.signout(authentication);
    //     return "redirect:/auth/home";
    // }

    // @PostMapping(value = "/signout")
    // public ResponseEntity<?> signout(@CurrentUser UserPrincipal userPrincipal
    //                                 , Authentication authentication){
    //     return authService.signout(authentication);   
    // }

    // @GetMapping(value = "/loginPage")
    // public String loginPage(ModelAndView model){
    //     model.addObject("SignInRequest", new SignInRequest());
    //     return "login";
    // }

    // @GetMapping(value = "/home")
    // public String home(){
    //     return "home";
    // }

    // @GetMapping(value="/customSignup")
    // public String customSignup(ModelAndView modelAndView){
    //     modelAndView.addObject("SignUpRequest", new SignUpRequest());
    //     return "customSignup";
    // }

   

    // @GetMapping(value = "/main")
    // public String main(){
    //     return "main";
    // }

    // @GetMapping(value = "/authtest")
    // public void authTest(){
    //     Object authentication = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    //     UserPrincipal userPrincipal = (UserPrincipal)authentication;
    //     System.out.println(userPrincipal.getName() + " " + userPrincipal.getUsername() + " " + userPrincipal.getAuthorities() + " " + userPrincipal.getPassword());
    //     System.out.println(userPrincipal.toString());
    // }

    // @GetMapping(value = "/menutest")
    // public String menutest(){
    //     return "menu/index";
    // }
}
