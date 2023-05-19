package com.example.jwt_oauth.auth;


import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import com.example.jwt_oauth.domain.dto.UserDto;
import com.example.jwt_oauth.domain.user.Provider;
import com.example.jwt_oauth.domain.user.Role;
import com.example.jwt_oauth.domain.user.User;
import com.example.jwt_oauth.domain.user.UserEnum;
import com.example.jwt_oauth.service.user.UserService;
import com.example.jwt_oauth.service.user.auth.CustomUserDetailsService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WithAccountSecurityContextFactory implements WithSecurityContextFactory<WithAccount> {

    private final UserService userService;

    private final CustomUserDetailsService customUserDetailsService;

    @Override
    public SecurityContext createSecurityContext(WithAccount withAccount) {
        String nickname = withAccount.value();

        // SignUpForm signUpForm = new SignUpForm();
        // signUpForm.setNickname(nickname);
        // signUpForm.setEmail(nickname + "@tistory.com");
        // signUpForm.setPassword("12341234");
        // accountService.processNewAccount(signUpForm);
        User user = User.builder()
                        .name(nickname)
                        .email("1234@naver.com")
                        .imageUrl("imageUrltest")
                        .emailVerified(true)
                        .password("1234")
                        .provider(Provider.local)
                        .role(Role.ADMIN)
                        // .UserEnum(UserEnum.Y)
                        .build();
        userService.create(UserDto.builder()
                                    .name(user.getName())
                                    .email(user.getEmail())
                                    .imageUrl(user.getImageUrl())
                                    .emailVerified(user.getEmailVerified())
                                    .password(user.getPassword())
                                    .provider(user.getProvider())
                                    .role(user.getRole())
                                    .build());

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getEmail());
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        

        return securityContext;
    }
    

    
}
