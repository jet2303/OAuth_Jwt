package com.example.jwt_oauth.service.user.auth;

import java.util.Optional;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.example.jwt_oauth.config.security.auth.OAuth2UserInfo;
import com.example.jwt_oauth.config.security.auth.OAuth2UserInfoFactory;
import com.example.jwt_oauth.config.security.token.UserPrincipal;
import com.example.jwt_oauth.domain.user.Provider;
import com.example.jwt_oauth.domain.user.Role;
import com.example.jwt_oauth.domain.user.User;
import com.example.jwt_oauth.repository.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomDefaultOAuth2UserService extends DefaultOAuth2UserService{

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        try{
            return processOAuth2User(userRequest, oAuth2User);
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;

    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User){

        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(oAuth2UserRequest.getClientRegistration().getClientId(), oAuth2User.getAttributes());

        Optional<User> userOptional = userRepository.findByEmail(oAuth2UserInfo.getEmail());
        User user;
        if(userOptional.isPresent()){
            user = userOptional.get();
            user = updateExistingUser(user, oAuth2UserInfo);
        }else{
            user = registerNewUser(oAuth2UserRequest, oAuth2UserInfo);
        }
        

        return UserPrincipal.create(user, oAuth2User.getAttributes());
    }

    private User registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo){
        User user = User.builder()
                        .provider(Provider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()))
                        .providerId(oAuth2UserInfo.getId())
                        .name(oAuth2UserInfo.getName())
                        .email(oAuth2UserInfo.getEmail())
                        .imageUrl(oAuth2UserInfo.getImageUrl())
                        .role(Role.USER)
                        .build();

        return userRepository.save(user);
    }


    private User updateExistingUser(User user, OAuth2UserInfo oAuth2UserInfo){

        user.updateName(oAuth2UserInfo.getName());
        user.updateImageUrl(oAuth2UserInfo.getImageUrl());

        return userRepository.save(user);
    }
    
}
