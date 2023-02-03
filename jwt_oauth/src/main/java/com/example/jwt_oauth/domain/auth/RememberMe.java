package com.example.jwt_oauth.domain.auth;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Builder
@AllArgsConstructor
@Getter
@Setter
public class RememberMe {
    
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String series;

    private String token;

    private String userName;

    private Date lastLogin;

}
