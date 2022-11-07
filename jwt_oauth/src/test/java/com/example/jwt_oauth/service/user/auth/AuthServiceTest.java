package com.example.jwt_oauth.service.user.auth;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.jwt_oauth.domain.user.Token;
import com.example.jwt_oauth.payload.request.auth.SignInRequest;
import com.example.jwt_oauth.payload.request.auth.SignUpRequest;
import com.example.jwt_oauth.payload.response.ApiResponse;
import com.example.jwt_oauth.repository.auth.TokenRepository;
import com.example.jwt_oauth.repository.user.UserRepository;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
public class AuthServiceTest {


    @Autowired
    private AuthService authService;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    void testDelete() {

    }

    @Test
    void testModify() {

    }

    @Test
    void testRefresh() {

    }

    @Test
    @Order(2)
    @Disabled
    void testSignin() {
        log.info("token repository {}",tokenRepository.findAll()); 

        SignInRequest signInRequest = new SignInRequest();
        signInRequest.setEmail("test@naver.com");
        signInRequest.setPassword("password");


        ResponseEntity<Token> token = authService.signin(signInRequest);
        log.info("{}", token.getBody());
    }

    @Test
    void testSignout() {

    }

    @Test
    @Order(1)
    void testSignup() {
        

        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setEmail("test@naver.com");
        signUpRequest.setName("nameTest");
        signUpRequest.setPassword("password");

        ResponseEntity<ApiResponse> responseEntity = authService.signup(signUpRequest);
        log.info("{} , {}", responseEntity.getBody(), userRepository.findAll());
////////////////////////////////////////////
        log.info("token repository {}",tokenRepository.findAll()); 

        SignInRequest signInRequest = new SignInRequest();
        signInRequest.setEmail("test@naver.com");
        signInRequest.setPassword(bCryptPasswordEncoder.encode("password"));


        ResponseEntity<Token> token = authService.signin(signInRequest);
        log.info("{}", token.getBody());
    }

    @Test
    void testWhoAmI() {

    }
}
