package com.example.jwt_oauth.config.security.auth;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.jwt_oauth.payload.error.ErrorCode;
import com.example.jwt_oauth.payload.error.RestApiException;
import com.example.jwt_oauth.payload.error.errorCodes.UserErrorCode;
import com.example.jwt_oauth.service.user.auth.CustomUserDetailsService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider{

    private final CustomUserDetailsService customUserDetailsService;

    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        if(userDetails==null){
            // throw new BadCredentialsException("등록되지 않은 사용자.");
            throw new RestApiException(UserErrorCode.NOT_FOUND_USER);
        }
        if(!passwordEncoder.matches(password, userDetails.getPassword())){
            // throw new BadCredentialsException("등록되지 않은 사용자.");
            throw new RestApiException(UserErrorCode.NOT_MATCHED_PASSWORD);
        }

        UsernamePasswordAuthenticationToken authenticationToken 
                        = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null, userDetails.getAuthorities());

        return authenticationToken;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
    
}
