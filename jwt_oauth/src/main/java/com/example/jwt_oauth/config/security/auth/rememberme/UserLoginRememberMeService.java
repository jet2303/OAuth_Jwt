package com.example.jwt_oauth.config.security.auth.rememberme;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.Base64.Encoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;
import org.springframework.security.web.authentication.rememberme.CookieTheftException;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationException;

import com.example.jwt_oauth.domain.auth.RememberMe;
import com.example.jwt_oauth.domain.auth.RememberMeRepository;
import com.example.jwt_oauth.service.user.auth.CustomUserDetailsService;

import lombok.AllArgsConstructor;


public class UserLoginRememberMeService extends AbstractRememberMeServices{

    @Autowired
    private RememberMeRepository rememberMeRepository;

    SecureRandom random;

    public UserLoginRememberMeService(String key, CustomUserDetailsService customUserDetailsService){
        super(key, customUserDetailsService);
        random = new SecureRandom();
    }

    @Override
    protected void onLoginSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication successfulAuthentication) {
        
        String cookie = super.extractRememberMeCookie(request);

        // 기존에 생성된 cookie가 있을 경우 DB에서 삭제
        //if()

        String userName = successfulAuthentication.getName();
        String seriesValue = generateTokenValue();
        String tokenValue = generateTokenValue();

        try{
            RememberMe rememberMe = new RememberMe(seriesValue, tokenValue, userName, new Date());
            rememberMeRepository.save(rememberMe);

            String[] rawCookieValues = new String[] { seriesValue, tokenValue };
            super.setCookie(rawCookieValues, getTokenValiditySeconds(), request, response);
        }catch(DataAccessException e){
            e.printStackTrace();
        }
        

    }

    @Override
    protected UserDetails processAutoLoginCookie(String[] cookieTokens, HttpServletRequest request,
            HttpServletResponse response) throws RememberMeAuthenticationException, UsernameNotFoundException {
        
        // 쿠키 : series, token
		// 포함된 값이 2개가 아닌 경우
        if(cookieTokens.length != 2){
            throw new RuntimeException("invalid Cookieeeeeeeeeeee");
        }
        
        String cookieSeries = cookieTokens[0];
        String cookieToken = cookieTokens[1];

        RememberMe rememberMe = rememberMeRepository.findBySeries(cookieSeries).get();

        // Series 없는경우
        if(rememberMe == null){
            throw new RememberMeAuthenticationException("not find Series");
        }

        // 변조된 쿠키로 인증시도 시
        if(!cookieToken.equals(rememberMe.getToken())){
            //해당 Series 삭제
            rememberMeRepository.deleteBySeries(cookieSeries);

            throw new CookieTheftException("forgery cookieeeeeeeeeeee");
        }

        // 유효기간 검증
        // 10분 하드코딩 박아놓은것 validtime 함수로 수정
        if(rememberMe.getLastLogin().getTime() + (1000L * 60L) * 1000L < System.currentTimeMillis()){
            rememberMeRepository.deleteBySeries(cookieSeries);
            throw new RememberMeAuthenticationException("expiration date Cookieeeeeeeeeee");
        }

        String newToken = generateTokenValue();
		rememberMe.setToken(newToken);
		rememberMe.setLastLogin(new Date());

        try{
            // RememberMe updateRememberMe = rememberMeRepository.updateBySeries(rememberMe.getToken(), rememberMe.getLastLogin(), rememberMe.getSeries()).get();

            String[] rawCookieValues = new String[] {cookieSeries, cookieToken};
            super.setCookie(rawCookieValues, 1000 * 60, request, response);
        }catch(DataAccessException e){
            // throw new RuntimeException("cannot access dataaaaaaaaaaaaaaaaaa");
            e.printStackTrace();
        }

        
        return getUserDetailsService().loadUserByUsername(rememberMe.getUserName());
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // TODO Auto-generated method stub
        super.logout(request, response, authentication);
    }

    private String generateTokenValue() {
		byte[] newToken = new byte[16];
		random.nextBytes(newToken);
        Encoder encoder = Base64.getEncoder();
		return new String(encoder.encode(newToken));
	}
}
