package com.example.jwt_oauth.service.user.auth;

import java.net.URI;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
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

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final AuthenticationManager authenticationManager;
    // @Autowired(required = true)
    // private AuthenticationManager authenticationManager;
    
    private final PasswordEncoder passwordEncoder;
    private final CustomTokenProviderService customTokenProviderService;

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    public ResponseEntity<?> whoAmI(UserPrincipal userPrincipal){
        Optional<User> user = userRepository.findById(userPrincipal.getId());

        return ResponseEntity.ok(user.get());
    }

    public ResponseEntity<?> delete(UserPrincipal userPrincipal){
        Optional<User> user = userRepository.findById(userPrincipal.getId());

        Optional<Token> token = tokenRepository.findByUserEmail(userPrincipal.getEmail());

        userRepository.delete(user.get());
        tokenRepository.delete(token.get());


        return ResponseEntity.ok("delete 완료");
    }

    public ResponseEntity<?> modify(UserPrincipal userPrincipal, ChangePasswordRequest changePasswordRequest){
        Optional<User> user = userRepository.findById(userPrincipal.getId());
        Boolean chkPassword = passwordEncoder.matches(changePasswordRequest.getOldPassword(), 
                                                        user.get().getPassword() );
        chkPassword(chkPassword);
        Boolean chkNewPassword = changePasswordRequest.getNewPassword().equals(changePasswordRequest.getChkNewPassword());
        chkPassword(chkNewPassword);
        
        return ResponseEntity.ok(true);
    }

    public ResponseEntity<Token> signin(SignInRequest signInRequest){

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(signInRequest.getEmail(), signInRequest.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        TokenMapping tokenMapping = customTokenProviderService.createToken(authentication);
        Token token = Token.builder()    
                                .userEmail(tokenMapping.getUserEmail())
                                .refreshToken(tokenMapping.getRefreshToken())
                                .build();
        // tokenRepository.save(token);
        
        return ResponseEntity.ok(tokenRepository.save(token));
    }

    public ResponseEntity<ApiResponse> signup(SignUpRequest signUpRequest){
        
        User user = User.builder()
                            .name(signUpRequest.getName())
                            .email(signUpRequest.getEmail())
                            .password(signUpRequest.getPassword())
                            .provider(Provider.local)
                            .role(Role.ADMIN)
                            .build();

        userRepository.save(user);
        
        URI location = ServletUriComponentsBuilder
                            .fromCurrentContextPath().path("/auth/")
                            .buildAndExpand(user.getId()).toUri();

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

    public ResponseEntity<?> signout(RefreshTokenRequest refreshTokenRequest){
        boolean checkValid = valid(refreshTokenRequest.getRefreshToken());

        Optional<Token> token = tokenRepository.findByRefreshToken(refreshTokenRequest.getRefreshToken());
        tokenRepository.delete(token.get());
        ApiResponse apiResponse = ApiResponse.builder()
                                                .check(true)
                                                .information(Message.builder()
                                                                    .message("success logout")
                                                                    .build() )
                                                .build();
        return ResponseEntity.ok(apiResponse);                                                
    }

    private boolean valid(String refreshToken){

        boolean validateCheck = customTokenProviderService.validateToken(refreshToken);

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
