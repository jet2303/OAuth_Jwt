package com.example.jwt_oauth.Renewal_230524.Service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.example.jwt_oauth.config.security.token.UserPrincipal;
import com.example.jwt_oauth.domain.user.Provider;
import com.example.jwt_oauth.domain.user.Role;
import com.example.jwt_oauth.domain.user.User;
import com.example.jwt_oauth.domain.user.UserEnum;
import com.example.jwt_oauth.payload.error.RestApiException;
import com.example.jwt_oauth.payload.request.auth.SignInRequest;
import com.example.jwt_oauth.payload.response.ExceptionResponse;
import com.example.jwt_oauth.payload.response.auth.AuthResponse;
import com.example.jwt_oauth.repository.user.UserRepository;
import com.example.jwt_oauth.service.user.auth.AuthService;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
@Transactional
public class AuthServiceTest extends MockBeans {

    @BeforeEach
    public void setUp() {
        User user = new User("setUpName", "setup@naver.com", "imageUrl", true,
                passwordEncoder.encode("test"), Provider.local, Role.ADMIN, "providerId", UserEnum.Y);
        userRepository.save(user);
    }

    // login
    @Test
    public void 로그인성공() {
        SignInRequest request = SignInRequest.builder()
                .email("setup@naver.com")
                .password("test")
                .rememberMe("rememberMe")
                .build();
        ResponseEntity<AuthResponse> authResponse = authService.signin(request, mockHttpServletResponse);

        assertNotNull(authResponse.getBody().getAccessToken());

    }

    // login 실패 - id - 에러 return
    @Test
    public void 로그인실패_id오류() {
        SignInRequest request = SignInRequest.builder()
                .email("test111@naver.com")
                .password("admin")
                .rememberMe("rememberMe")
                .build();

        assertThrows(RestApiException.class, () -> {
            authService.signin(request, mockHttpServletResponse).getBody().getAccessToken();
        });

    }
    // login 실패 - pw - 에러 return

    @Test
    public void 로그인실패_PW오류() {
        SignInRequest request = SignInRequest.builder()
                .email("test1@naver.com")
                .password("admin1")
                .rememberMe("rememberMe")
                .build();

        assertThrows(RestApiException.class, () -> {
            authService.signin(request, mockHttpServletResponse).getBody().getAccessToken();
        });
    }

    // logout
    @Test
    public void 로그아웃() {
        SignInRequest request = SignInRequest.builder()
                .email("setup@naver.com")
                .password("test")
                .rememberMe("rememberMe")
                .build();
        ResponseEntity<AuthResponse> authResponse = authService.signin(request, mockHttpServletResponse);
        assertTrue(!authResponse.getBody().getAccessToken().isEmpty());
        // Authentication authentication =
        // SecurityContextHolder.getContext().getAuthentication();

        // UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        authService.signout(SecurityContextHolder.getContext().getAuthentication(), mockHttpServletResponse);

        assertFalse(authResponse.getBody().getAccessToken().isEmpty());

        assertThrows(NullPointerException.class,
                () -> SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }
    // logout 실패
    // @Test
    // public void 로그아웃_실패(){

    // }

    // jwt token 만료

    // jwt refresh token 만료

    // @after 계정 삭제

}
