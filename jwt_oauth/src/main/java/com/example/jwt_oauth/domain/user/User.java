package com.example.jwt_oauth.domain.user;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.example.jwt_oauth.domain.time.DefaultTime;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;


@Entity
@Getter
@Builder
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name="_user")
@ToString
@DynamicUpdate
public class User extends DefaultTime{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @Column(name = "account")
    private String name;

    @Email
    private String email;

    private String imageUrl;

    private Boolean emailVerified;

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




    // public User(Long id,String name, String email, UserEnum useyn){
    //     this.id = id;
    //     this.name = name;
    //     this.email = email;
    //     this.useyn = useyn;
    // }

    @Builder
    public User(String name, String email, String imageUrl, Boolean emailVerifired, 
                String password, Provider provider, Role role, String providerId, UserEnum useyn){
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
