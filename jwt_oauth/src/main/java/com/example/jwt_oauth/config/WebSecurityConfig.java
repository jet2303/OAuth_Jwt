package com.example.jwt_oauth.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.RememberMeAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.web.servlet.ViewResolver;
import org.thymeleaf.spring5.ISpringTemplateEngine;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import com.example.jwt_oauth.config.security.handler.CustomSimpleUrlAuthenticationFailureHandler;
import com.example.jwt_oauth.config.security.handler.CustomSimpleUrlAuthenticationSuccessHandler;
import com.example.jwt_oauth.config.security.handler.loginhandler.CustomUrlAuthenticationFailureHandler;
import com.example.jwt_oauth.config.security.handler.loginhandler.CustomUrlAuthenticationSuccessHandler;
import com.example.jwt_oauth.config.security.token.CustomOncePerRequestFilter;
import com.example.jwt_oauth.repository.auth.CustomAuthorizationRequestRepository;

import com.example.jwt_oauth.service.user.auth.CustomDefaultOAuth2UserService;
import com.example.jwt_oauth.service.user.auth.CustomUserDetailsService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final CustomSimpleUrlAuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    // private final AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final CustomSimpleUrlAuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
    // private final AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
    
    private final CustomAuthorizationRequestRepository customAuthorizationRequestRepository;    
    private final CustomDefaultOAuth2UserService customOAuth2UserService;
    // private final CustomOncePerRequestFilter customOncePerRequestFilter;

    private final CustomUrlAuthenticationSuccessHandler customUrlAuthenticationSuccessHandler;
    private final CustomUrlAuthenticationFailureHandler customUrlAuthenticationFailureHandler;

    private final CustomUserDetailsService customUserDetailsService;

    private final DataSource dataSource;
    

    
    private final String REMEMBER_ME_KEY = "remember_key";

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    
        http.authorizeRequests()            
                .antMatchers("/h2-console/**").permitAll()                    
                .antMatchers("/auth/**").permitAll()
                .antMatchers("/loginPage", "/customSignup","/signin", "/signout","/home", "/authtest", "/main").permitAll()
                .antMatchers("/list/**").permitAll()
                .anyRequest().authenticated()
                .and()
            .headers()
                .frameOptions().disable()
                .and()       

            // .rememberMe()
            //     .key("uniqueAndSecret")
            //     .rememberMeParameter("remember-me")
            //     .userDetailsService(customUserDetailsService)
            //     .tokenValiditySeconds(86400 * 30)
            //     .userDetailsService(customUserDetailsService)
            //     .tokenRepository(remembermeTokenRepository)
            //     .rememberMeServices(persistentTokenBasedRememberMeServices())
            //     .authenticationSuccessHandler(customUrlAuthenticationSuccessHandler)
            //     .and()
            .rememberMe() // 사용자 계정 저장
                .rememberMeServices(persistentTokenBasedRememberMeServices())
                .and()
            .formLogin()
                .permitAll()
                
                .loginPage("/loginPage")
                
                .usernameParameter("email")
                .passwordParameter("password")
                .successHandler(authenticationSuccessHandler())
                .failureHandler(customUrlAuthenticationFailureHandler)
                .and()
            .logout()
                .permitAll()
                .and()
            .csrf()
                .disable()
                // .setSharedObject(RememberMeServices.class, tokenBasedRememberMeServices())
            
            .oauth2Login()
                .loginPage("/auth/loginPage")
                .authorizationEndpoint()
                    .baseUri("/oauth2/authorize")
                    .authorizationRequestRepository(customAuthorizationRequestRepository)
                    .and()
                .redirectionEndpoint()
                    .baseUri("/oauth2/callback/*")
                    .and()
                .userInfoEndpoint()
                    .userService(customOAuth2UserService)
                    .and()
                .successHandler(oAuth2AuthenticationSuccessHandler)
                .failureHandler(oAuth2AuthenticationFailureHandler)
                ;
        //form login 인증처리를 하는 기본 필터 UsernamePasswordAuthenticationFilter
        http.addFilterBefore(customOncePerRequestFilter(), UsernamePasswordAuthenticationFilter.class);
        
            
        

        http.headers().frameOptions().sameOrigin();
        return http.build();
    }
     
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
         
        return (web) -> web.ignoring().antMatchers("/images/**", "/js/**", "/webjars/**");
         
    }

    //doFilterInternal Fileter 등록
    @Bean
    public CustomOncePerRequestFilter customOncePerRequestFilter() {
        return new CustomOncePerRequestFilter();
    }
    

    //////////////////////////////////////////////////////////////////////////////////////////////////
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CustomSimpleUrlAuthenticationFailureHandler loginFailureHandler(){
        return new CustomSimpleUrlAuthenticationFailureHandler(customAuthorizationRequestRepository);
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler(){
        return new CustomUrlAuthenticationFailureHandler();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler(){
        return new CustomUrlAuthenticationSuccessHandler();
    }

    // @Bean
    // public RememberMeAuthenticationFilter rememberMeAuthenticationFilter() throws Exception{
    //     // TokenBasedRememberMeServices tokenBasedRememberMeServices = new TokenBasedRememberMeServices(REMEMBER_ME_KEY, customUserDetailsService);
        
    //     return ;
    // }

    // @Bean
    // PersistentTokenBasedRememberMeServices persistentTokenBasedRememberMeServices() {
    //     return new PersistentTokenBasedRememberMeServices(
    //             "hello", customUserDetailsService, persistTokenRepository());
    // }
    
    // @Bean
    // PersistentTokenRepository persistTokenRepository() {
    //     JdbcTokenRepositoryImpl repository = new JdbcTokenRepositoryImpl();
    //     repository.setDataSource(dataSource);
    //     try {
    //         repository.removeUserTokens("1");
    //     } catch(Exception ex) {
    //         repository.setCreateTableOnStartup(true);
    //     }

    //     return repository;
    // }

    // 
    @Bean
    JdbcTokenRepositoryImpl jdbcTokenRepositoryImpl(){
        JdbcTokenRepositoryImpl repository = new JdbcTokenRepositoryImpl();
        repository.setDataSource(dataSource);
        try {
            repository.removeUserTokens("1");
        } catch(Exception ex) {
            repository.setCreateTableOnStartup(true);
        }

        return repository;
    }

    @Bean
    PersistentTokenBasedRememberMeServices persistentTokenBasedRememberMeServices(){
        PersistentTokenBasedRememberMeServices persistentTokenBasedRememberMeServices = 
            new PersistentTokenBasedRememberMeServices("hello", customUserDetailsService, jdbcTokenRepositoryImpl());
        
        return persistentTokenBasedRememberMeServices;
    }

    // @Bean
    // public TokenBasedRememberMeServices tokenBasedRememberMeServices(){
    //     TokenBasedRememberMeServices rememberMeServices = new TokenBasedRememberMeServices(REMEMBER_ME_KEY, customUserDetailsService);
    //     rememberMeServices.setTokenValiditySeconds(60*60*24);
    //     rememberMeServices.setCookieName("remember-me-cookie-name");
    //     rememberMeServices.setParameter("remember-me");
    //     return rememberMeServices;
    // }

    // @Bean
    // public RememberMeAuthenticationProvider rememberMeAuthenticationProvider(){
    //     return new RememberMeAuthenticationProvider(REMEMBER_ME_KEY);
    // }

    
    
    // @Bean
    // public PersistentTokenBasedRememberMeServices persistentTokenBasedRememberMeServices(){
    //     PersistentTokenBasedRememberMeServices persistentTokenBasedRememberMeServices 
    //         = new PersistentTokenBasedRememberMeServices("rememberKey", customUserDetailsService, remembermeTokenRepository);
    //     persistentTokenBasedRememberMeServices.setParameter("remember-me");
    //     persistentTokenBasedRememberMeServices.setAlwaysRemember(false);
    //     persistentTokenBasedRememberMeServices.setCookieName("remember-me");
    //     persistentTokenBasedRememberMeServices.setTokenValiditySeconds(86400 * 30);
    //     return persistentTokenBasedRememberMeServices;
    // }
    
}
