package com.example.jwt_oauth.service.user.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.jwt_oauth.domain.user.Token;
import com.example.jwt_oauth.domain.user.User;
import com.example.jwt_oauth.payload.request.auth.SignInRequest;
import com.example.jwt_oauth.payload.request.auth.SignUpRequest;
import com.example.jwt_oauth.payload.response.ApiResponse;
import com.example.jwt_oauth.payload.response.AuthResponse;
import com.example.jwt_oauth.payload.response.Message;
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

    // @Autowired
    // private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private PasswordEncoder passwordEncoder;

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
    
    void testSignin() {
        SignInRequest signInRequest = new SignInRequest();
        signInRequest.setEmail("test@naver.com");
        signInRequest.setPassword("password");
        // signInRequest.setPassword(passwordEncoder.encode("password"));

        
        ResponseEntity<AuthResponse> result = authService.signin(signInRequest);
        
        
        log.info("{}", result.getBody().getAccessToken());
        assertTrue(!result.getBody().getRefreshToken().isEmpty());
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

        assertEquals(Message.builder().message("signup success").build()
                , responseEntity.getBody().getInformation());
    
    }

    @Test
    void testWhoAmI() {

    }
}
