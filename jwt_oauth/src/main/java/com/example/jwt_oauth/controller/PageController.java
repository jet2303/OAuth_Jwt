package com.example.jwt_oauth.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.example.jwt_oauth.config.security.token.UserPrincipal;
import com.example.jwt_oauth.payload.request.auth.SignInRequest;
import com.example.jwt_oauth.payload.request.auth.SignUpRequest;
import com.example.jwt_oauth.service.user.auth.AuthService;

import lombok.RequiredArgsConstructor;

@Controller
// @RequiredArgsConstructor
public class PageController {

    @GetMapping(value = "/loginPage")
    public String loginPage(ModelAndView model){
        model.addObject("signInRequest", new SignInRequest());
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
    
    @GetMapping(value = "/main")
    public String main(){
        return "main";
    }

    @GetMapping(value = "/authtest")
    public void authTest(){
        Object authentication = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserPrincipal userPrincipal = (UserPrincipal)authentication;
        System.out.println(userPrincipal.getName() + " " + userPrincipal.getUsername() + " " + userPrincipal.getAuthorities() + " " + userPrincipal.getPassword());
        System.out.println(userPrincipal.toString());
    }

    @GetMapping(value = "/menutest")
    public String menutest(){
        return "menu/index";
    }
}
