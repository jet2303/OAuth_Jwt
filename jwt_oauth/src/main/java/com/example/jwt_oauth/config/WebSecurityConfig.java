package com.example.jwt_oauth.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
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

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    
        http.authorizeRequests()            
                .antMatchers("/h2-console/**").permitAll()                    
                .antMatchers("/auth/**").permitAll()
                // .antMatchers("/auth/loginPage", "/auth/customSignup","/auth/signin", "/auth/signout","/auth/home").permitAll()
                .anyRequest().authenticated()
                .and()
            .headers()
                .frameOptions().disable()
                .and()                
            .formLogin()
                .permitAll()
                
                .loginPage("/auth/loginPage")
                
                .usernameParameter("email")
                .passwordParameter("password")
                .successHandler(customUrlAuthenticationSuccessHandler)
                // .successHandler(new AuthenticationSuccessHandler() {
                    
                //     public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                //     Authentication authentication) throws IOException, ServletException{
                //         System.out.println("authentication : " + authentication.getName());
                //         response.sendRedirect("/auth/main");
                //     }
                // })
                .failureHandler(authenticationFailureHandler())
                // .failureHandler(customUrlAuthenticationFailureHandler)

                // .failureHandler(new AuthenticationFailureHandler() {
                //     public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			    //                                         AuthenticationException exception) throws IOException, ServletException{
                //         System.out.println("exception : " + exception.getMessage());
                //         response.sendRedirect("/auth/home");
                //     }
                // })
                .and()                
            .logout()
                .permitAll()
                .and()
            .csrf()
                .disable()
                            
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
        //인증처리를 하는 기본 필터 UsernamePasswordAuthenticationFilter
        http.addFilterBefore(customOncePerRequestFilter(), UsernamePasswordAuthenticationFilter.class);

        http.headers().frameOptions().sameOrigin();
        return http.build();
    }
     
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
         
        return (web) -> web.ignoring().antMatchers("/images/**", "/js/**", "/webjars/**");
         
    }

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
    
    
}
