package com.example.jwt_oauth.domain.dto;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

import com.example.jwt_oauth.domain.user.Provider;
import com.example.jwt_oauth.domain.user.Role;
import com.example.jwt_oauth.domain.user.UserEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Builder
@Getter
@Setter
public class UserDto {

    private Long id;

    private String name;

    private String email;

    @Enumerated
    private UserEnum useyn;

    private String imageUrl;

    private Boolean emailVerified = false;

    @JsonIgnore
    private String password;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Provider provider;

    private String providerId;

    @Enumerated(EnumType.STRING)
    private Role role;


    @Builder
    public UserDto(Long id, String name, String email, UserEnum useyn, String imageUrl, Boolean emailVerifired, 
                    String password, Provider provider, Role role, String providerId){
        this.id = id;
        this.name = name;
        this.email = email;
        this.useyn = useyn;
        this.imageUrl = imageUrl;
        this.emailVerified = emailVerifired;
        this.password = password;
        this.provider = provider;
        this.role = role;
        this.providerId = providerId;
    }
  
}
