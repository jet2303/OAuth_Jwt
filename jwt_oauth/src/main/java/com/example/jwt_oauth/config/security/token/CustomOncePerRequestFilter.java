package com.example.jwt_oauth.config.security.token;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.jwt_oauth.service.user.auth.CustomTokenProviderService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomOncePerRequestFilter extends OncePerRequestFilter{

    //생성자 주입으로 해결되는지 확인.
    @Autowired
    private CustomTokenProviderService customTokenProviderService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String jwt = getJwtFromRequest(request);

        if(StringUtils.hasText(jwt) && customTokenProviderService.validateToken(jwt)){
            UsernamePasswordAuthenticationToken authentication = customTokenProviderService.getAuthenticationById(jwt);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }
    
    public String getJwtFromRequest(HttpServletRequest request){
        String bearerToken = request.getHeader("Authorization");
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")){
            log.info("bearerToken = {}", bearerToken.substring(7, bearerToken.length()));
            return bearerToken.substring(7, bearerToken.length());
        }
        return null;
    }

    // 비동기 통신을 위한 AJAX filter 
    // private AntPathRequestMatcher antPathRequestMatcher() {
    //     return new AntPathRequestMatcher(TOKEN_END_POINT, HttpMethod.POST.name());
    // }
     
    // public AjaxAuthenticationFilter ajaxAuthenticationFilter() throws Exception {
    //     AjaxAuthenticationFilter filter = new AjaxAuthenticationFilter(antPathRequestMatcher(), objectMapper);
    //     filter.setAuthenticationManager(authenticationManager());
    //     filter.setAuthenticationSuccessHandler(securityHandler);
    //     filter.setAuthenticationFailureHandler(securityHandler);
    //     return filter;
    // }
}
