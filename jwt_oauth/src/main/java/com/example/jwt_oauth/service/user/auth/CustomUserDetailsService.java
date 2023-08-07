package com.example.jwt_oauth.service.user.auth;


import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationException;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter;
import org.springframework.stereotype.Service;

import com.example.jwt_oauth.config.security.token.UserPrincipal;
import com.example.jwt_oauth.domain.user.User;
import com.example.jwt_oauth.payload.error.RestApiException;
import com.example.jwt_oauth.payload.error.errorCodes.UserErrorCode;
import com.example.jwt_oauth.repository.user.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService{
    
    private final UserRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        
        User user = userRepository.findByEmail(email).orElseThrow( () -> new RestApiException(UserErrorCode.NOT_FOUND_USER));
        return UserPrincipal.create(user);
    }

    @Transactional
    public UserDetails loadUserById(Long id){
        Optional<User> user = userRepository.findById(id);
        // DefaultAssert.isOptionalPresent(user);

        return UserPrincipal.create(user.get());
    }
    
    
}
