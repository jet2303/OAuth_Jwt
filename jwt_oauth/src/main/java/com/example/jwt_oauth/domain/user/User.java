package com.example.jwt_oauth.domain.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import com.example.jwt_oauth.domain.time.DefaultTime;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;


@Entity
@Getter
@Builder
@RequiredArgsConstructor
@EqualsAndHashCode
@Table(name="_user")
public class User extends DefaultTime{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @Column(name = "account")
    private String name;

    @Email
    private String email;

    private String imageUrl;

    private Boolean emailVerified = false;

    @JsonIgnore
    private String password;

    // @NotNull
    @Enumerated(EnumType.STRING)
    private Provider provider;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String providerId;

    @Enumerated
    private UserEnum useyn;

    // public User(){

    // }

    // public User(Long id,String name, String email, UserEnum useyn){
    //     this.id = id;
    //     this.name = name;
    //     this.email = email;
    //     this.useyn = useyn;
    // }

    @Builder
    public User(String name, String email, UserEnum useyn, String imageUrl, Boolean emailVerifired, 
    String password, Provider provider, Role role, String providerId){
        this.name = name;
        this.email = email;
        this.useyn = useyn;
        this.imageUrl = imageUrl;
        this.emailVerified = emailVerifired;
        this.password = password;
        this.provider = provider;
        this.providerId = providerId;
        this.role = role;
    }
    
    public void updateName(String name){
        this.name = name;
    }

    public void updateImageUrl(String imageUrl){
        this.imageUrl = imageUrl;
    }

    
}
