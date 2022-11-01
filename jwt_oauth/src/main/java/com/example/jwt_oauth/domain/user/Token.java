package com.example.jwt_oauth.domain.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.example.jwt_oauth.domain.time.DefaultTime;

import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
public class Token extends DefaultTime{
    
    @Id
    @Column(name = "user_email", length = 1024, nullable = false)
    private String userEmail;

    @Column(name = "refresh_token", length = 1024, nullable = false)
    private String refreshToken;

    public Token(){}

    public Token updateRefreshToken(String refreshToken){
        this.refreshToken = refreshToken;
        return this;
    }

    @Builder
    public Token(String userEmail, String refreshToken){
        this.userEmail = userEmail;
        this.refreshToken = refreshToken;
    }
}
