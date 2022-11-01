package com.example.jwt_oauth.repository.auth;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.jwt_oauth.domain.user.Token;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long>{

    Optional<Token> findByUserEmail(String userEmail);
    Optional<Token> findByRefreshToken(String refreshToken);
}
