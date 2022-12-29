package com.example.jwt_oauth.service.user.auth;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.jwt_oauth.config.security.token.UserPrincipal;
import com.example.jwt_oauth.domain.mapping.TokenMapping;
import com.example.jwt_oauth.domain.user.Provider;
import com.example.jwt_oauth.domain.user.Role;
import com.example.jwt_oauth.domain.user.Token;
import com.example.jwt_oauth.domain.user.User;
import com.example.jwt_oauth.payload.request.auth.ChangePasswordRequest;
import com.example.jwt_oauth.payload.request.auth.RefreshTokenRequest;
import com.example.jwt_oauth.payload.request.auth.SignInRequest;
import com.example.jwt_oauth.payload.request.auth.SignUpRequest;
import com.example.jwt_oauth.payload.response.ApiResponse;
import com.example.jwt_oauth.payload.response.AuthResponse;
import com.example.jwt_oauth.payload.response.Message;
import com.example.jwt_oauth.repository.auth.TokenRepository;
import com.example.jwt_oauth.repository.user.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    
    private final AuthenticationManager authenticationManager;
    // @Autowired(required = true)
    // private AuthenticationManager authenticationManager;
    
    private final PasswordEncoder passwordEncoder;
    private final CustomTokenProviderService customTokenProviderService;

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    public ResponseEntity<?> whoAmI(UserPrincipal userPrincipal){
        // Optional<User> user = userRepository.findById(userPrincipal.getId());
        Optional<User> user = userRepository.findByEmail(userPrincipal.getEmail());

        return ResponseEntity.ok(user.get());
    }

    public ResponseEntity<?> delete(UserPrincipal userPrincipal){
        Optional<User> user = userRepository.findById(userPrincipal.getId());

        Optional<Token> token = tokenRepository.findByUserEmail(userPrincipal.getEmail());

        userRepository.delete(user.get());
        tokenRepository.delete(token.get());


        return ResponseEntity.ok(ApiResponse.builder()
                                            .check(true)
                                            .information(Message.builder()
                                                                .message("Delete 성공")
                                                                .build())
                                            .build());
    }

    public ResponseEntity<ApiResponse> modify(UserPrincipal userPrincipal, ChangePasswordRequest changePasswordRequest){
        Optional<User> user = userRepository.findById(userPrincipal.getId());
        Boolean chkPassword = passwordEncoder.matches(changePasswordRequest.getOldPassword(), 
                                                        user.get().getPassword() );
        if(!chkPassword(chkPassword)){
            return ResponseEntity.ok(
                        ApiResponse.builder().check(true).information(
                                Message.builder().message("기존 비밀번호가 일치하지 않습니다.").build())
                        .build());
        }
        Boolean chkNewPassword = changePasswordRequest.getNewPassword().equals(changePasswordRequest.getChkNewPassword());
        if(!chkPassword(chkNewPassword)){
            return ResponseEntity.ok(
                        ApiResponse.builder().check(true).information(
                                Message.builder().message("새로운 비밀번호가 일치하지 않습니다.").build())
                        .build());
        }
        
        return ResponseEntity.ok(
            ApiResponse.builder()
                        .information(Message.builder().message("수정되었습니다.").build())
                        .build()
        );
    }

    public ResponseEntity<AuthResponse> signin(SignInRequest signInRequest, HttpServletResponse response){

        // log.info("{}", passwordEncoder.matches(signInRequest.getPassword(), userRepository.findByEmail(signInRequest.getEmail()).get().getPassword() ) );
        // log.info("{}, {}", signInRequest.getPassword(), userRepository.findByEmail(signInRequest.getEmail()).get().getPassword());

        //password 안맞는 경우 값이 return 되어 다음에 로그인 시도시 패스워드에 추가되어 request 되는 경우 수정할것.
        // try{
        //     Authentication authentication = authenticationManager.authenticate(
        //     new UsernamePasswordAuthenticationToken(signInRequest.getEmail(), signInRequest.getPassword())
        // );
        // }catch(BadCredentialsException e){
        //     e.printStackTrace();
        // }
        
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(signInRequest.getEmail(), signInRequest.getPassword())
        );
        if(!authentication.isAuthenticated()){
            response.setContentType("text/html; charset=euc-kr");
            
            try{
                PrintWriter out = response.getWriter();
                out.println("<script>alert('" + "등록되지않은 계정입니다." + "'); history.go(-1);</script>");
                out.flush();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);

        TokenMapping tokenMapping = customTokenProviderService.createToken(authentication);
        Token token = Token.builder()    
                                .userEmail(tokenMapping.getUserEmail())
                                .refreshToken(tokenMapping.getRefreshToken())
                                .build();
        tokenRepository.save(token);
        AuthResponse authResponse = AuthResponse.builder()
                                                .accessToken(tokenMapping.getAccessToken())
                                                .refreshToken(token.getRefreshToken())
                                                .build();
                                                
        URI location = ServletUriComponentsBuilder
                        .fromCurrentContextPath()
                        .path("/main")
                        // .buildAndExpand(user.getId())
                        .buildAndExpand()
                        .toUri();

        return ResponseEntity.created(location).body(authResponse);
        // return ResponseEntity.ok(authResponse);    

    }

    
    public ResponseEntity<ApiResponse> signup(SignUpRequest signUpRequest, HttpServletResponse response){
        
        Optional<User> userChk = userRepository.findByEmail(signUpRequest.getEmail());
        if(!userChk.isEmpty()){
            response.setContentType("text/html; charset=euc-kr");
            
            try{
                PrintWriter out = response.getWriter();
                out.println("<script>alert('" + "이미 등록된 계정입니다." + "'); history.go(-1);</script>");
                out.flush();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        if(signUpRequest.getRole()==null){
            response.setContentType("text/html; charset=euc-kr");
            try{
                PrintWriter out = response.getWriter();
                out.println("<script>alert('" + "권한을 체크하여주세요." + "'); history.go(-1);</script>");
                out.flush();

                
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        User user = User.builder()
                            .name(signUpRequest.getName())
                            .email(signUpRequest.getEmail())
                            .password(passwordEncoder.encode(signUpRequest.getPassword()))
                            .provider(Provider.local)
                            .role(Role.valueOf(signUpRequest.getRole()))
                            
                            .build();

        userRepository.save(user);
        
        
        URI location = ServletUriComponentsBuilder
                            .fromCurrentContextPath()
                            .path("/loginPage")
                            // .buildAndExpand(user.getId())
                            .buildAndExpand()
                            .toUri();
        
        List<User> users = userRepository.findAll();
        for (User listuser : users) {
            log.info("{}", listuser);
        }
        
        ApiResponse apiResponse = ApiResponse.builder()
                                                .check(true)
                                                .information(Message.builder().message("signup success").build())
                                                .build();       
        
        return ResponseEntity.created(location).body(apiResponse);  

    }

    public ResponseEntity<?> refresh(RefreshTokenRequest refreshTokenRequest){
        boolean checkValid = valid(refreshTokenRequest.getRefreshToken());

        Optional<Token> token = tokenRepository.findByRefreshToken(refreshTokenRequest.getRefreshToken());
        Authentication authentication = customTokenProviderService.getAuthenticationByEmail(token.get().getUserEmail());

        TokenMapping tokenMapping;

        Long expirationTime = customTokenProviderService.getExpiration(refreshTokenRequest.getRefreshToken());

        if(expirationTime > 0){
            tokenMapping = customTokenProviderService.refreshToken(authentication, token.get().getRefreshToken());
        }else{
            tokenMapping = customTokenProviderService.createToken(authentication);
        }

        Token updateToken = token.get().updateRefreshToken(tokenMapping.getRefreshToken());
        tokenRepository.save(updateToken);

        AuthResponse authResponse = AuthResponse.builder()      
                                                .accessToken(tokenMapping.getAccessToken())
                                                .refreshToken(tokenMapping.getRefreshToken())
                                                .build();

        return ResponseEntity.ok(authResponse);                                                
    }

    // public ResponseEntity<?> signout(RefreshTokenRequest refreshTokenRequest){
    public ResponseEntity<?> signout(Authentication authentication){
        String userEmail = authentication.getName();
        log.info("tokenrepository : {}", tokenRepository.findAll());
        Token userToken = tokenRepository.findByUserEmail(userEmail).get();
        
        boolean checkValid = valid(userToken.getRefreshToken());
        if(checkValid != false){
            Optional<Token> token = tokenRepository.findByRefreshToken(userToken.getRefreshToken());
            tokenRepository.delete(token.get());
            ApiResponse apiResponse = ApiResponse.builder()
                                                    .check(true)
                                                    .information(Message.builder()
                                                                        .message("success logout")
                                                                        .build() )
                                                    .build();
            SecurityContextHolder.clearContext();
            return ResponseEntity.ok(apiResponse);                                                
        }
        else{
            ApiResponse apiResponse = ApiResponse.builder()
                                                    .check(true)
                                                    .information(Message.builder()
                                                                        .message("failed logout")
                                                                        .build() )
                                                    .build();
            return ResponseEntity.ok(apiResponse);
        }
        
    }

    private boolean valid(String refreshToken){

        boolean validateCheck = customTokenProviderService.validateToken(refreshToken);
        log.info("{}",tokenRepository.findAll());
        Optional<Token> token = tokenRepository.findByRefreshToken(refreshToken);

        Authentication authentication = customTokenProviderService.getAuthenticationByEmail(token.get().getUserEmail());

        return true;
    }
    
    
    private boolean chkPassword(Boolean password){
        
        if(Boolean.TRUE.equals(password)){
            return true;
        }
        return false;
    }
}
