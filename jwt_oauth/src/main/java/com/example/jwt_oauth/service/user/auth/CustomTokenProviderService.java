package com.example.jwt_oauth.service.user.auth;

import java.security.Key;
import java.security.Principal;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.example.jwt_oauth.config.OAuth2Config;
import com.example.jwt_oauth.config.security.token.UserPrincipal;
import com.example.jwt_oauth.domain.mapping.TokenMapping;
import com.example.jwt_oauth.domain.user.User;
import com.example.jwt_oauth.repository.user.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
// 생성자 주입 수정
// @RequiredArgsConstructor
public class CustomTokenProviderService {

    @Autowired
    private OAuth2Config oAuth2Config;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private UserRepository userRepository;


    public TokenMapping refreshToken(Authentication authentication, String refreshToken){
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Date now = new Date();

        Date accessTokenExpiresIn = new Date(now.getTime() + oAuth2Config.getAuth().getAccessTokenExpirationMsec());

        String secretKey = oAuth2Config.getAuth().getTokenSecret();
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        Key key = Keys.hmacShaKeyFor(keyBytes);

        String accessToken = Jwts.builder()
                                    .setSubject(Long.toString(userPrincipal.getId()))
                                    .setIssuedAt(new Date())
                                    .setExpiration(accessTokenExpiresIn)
                                    .signWith(key)
                                    .compact();

        return TokenMapping.builder()     
                                .userEmail(userPrincipal.getEmail())
                                .accessToken(accessToken)
                                .refreshToken(refreshToken)
                                .build();
    }

    public TokenMapping createToken(Authentication authentication){
        //////////////////////////////////////// 07.03 AJS 수정 ////////////////////////////////////////
        String email = (String) authentication.getPrincipal();
        User findUser = userRepository.findByEmail(email).get();
        // User user = User.builder()
        //                 .id(findUser.getId())
        //                 .email(findUser.getEmail())
        //                 .password(findUser.getPassword())
        //                 .name(findUser.getName())
        //                 .role(findUser.getRole())
        //                 .build();
        UserPrincipal userPrincipal = UserPrincipal.create(findUser);
      
        /////////////////////////////////////////////////////////////////////////////////////////////////////

        // UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Date now = new Date();

        Date accessTokenExpiresIn = new Date(now.getTime() + oAuth2Config.getAuth().getAccessTokenExpirationMsec());
        Date refreshTokenExpiresIn = new Date(now.getTime() + oAuth2Config.getAuth().getRefreshTokenExpirationMsec());

        String secretKey = oAuth2Config.getAuth().getTokenSecret();

        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        Key key = Keys.hmacShaKeyFor(keyBytes);

        String accessToken = Jwts.builder()
                                    .setSubject(Long.toString(userPrincipal.getId()))
                                    .setIssuedAt(new Date())
                                    .setExpiration(accessTokenExpiresIn)
                                    .signWith(key)
                                    .compact();

        String refreshToken = Jwts.builder()
                                    .setExpiration(refreshTokenExpiresIn)
                                    .signWith(key, SignatureAlgorithm.HS512)
                                    .compact();

        return TokenMapping.builder()
                                .userEmail(userPrincipal.getEmail())
                                .accessToken(accessToken)
                                .refreshToken(refreshToken)
                                .build();
    }

    public Long getUserIdFromToken(String token){
        Claims claims = Jwts.parserBuilder()
                                .setSigningKey(oAuth2Config.getAuth().getTokenSecret())
                                .build()
                                .parseClaimsJws(token)
                                .getBody();
        return Long.parseLong(claims.getSubject());                                
    }

    public UsernamePasswordAuthenticationToken getAuthenticationById(String token){
        Long userId = getUserIdFromToken(token);
        UserDetails userDetails = customUserDetailsService.loadUserById(userId);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        return authentication;
    }

    public UsernamePasswordAuthenticationToken getAuthenticationByEmail(String email){
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        return authentication;
    }

    public Long getExpiration(String token){
        Date expiration = Jwts.parserBuilder()
                                .setSigningKey(oAuth2Config.getAuth().getTokenSecret())
                                .build()
                                .parseClaimsJws(token)
                                .getBody()
                                .getExpiration();
        Long now = new Date().getTime();
        return expiration.getTime() - now;
    }

    public boolean validateToken(String token){
        try{
            Jwts.parserBuilder()
                    .setSigningKey(oAuth2Config.getAuth().getTokenSecret())
                    .build()
                    .parseClaimsJws(token);
            return true;
        }catch(io.jsonwebtoken.security.SecurityException e){
            log.error("잘못된 JWT 서명");
        }catch(MalformedJwtException e){
            log.error("잘못된 JWT 서명");
        }catch(ExpiredJwtException e){
            log.error("만료된 JWT 토큰");
        }catch(UnsupportedJwtException e){
            log.error("지원되지않는 JWT 토큰");
        }catch(IllegalArgumentException e){
            log.error("JWT 토큰이 잘못되었음.");
        }
        return false;
    }
}
