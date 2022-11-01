package com.example.jwt_oauth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.jwt_oauth.config.security.handler.CustomSimpleUrlAuthenticationFailureHandler;
import com.example.jwt_oauth.config.security.handler.CustomSimpleUrlAuthenticationSuccessHandler;
import com.example.jwt_oauth.config.security.token.CustomOncePerRequestFilter;
import com.example.jwt_oauth.repository.auth.CustomAuthorizationRequestRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final CustomSimpleUrlAuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final CustomSimpleUrlAuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
    private final CustomAuthorizationRequestRepository customAuthorizationRequestRepository;
    // private final CustomOncePerRequestFilter customOncePerRequestFilter;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    
        http.authorizeRequests()            
                .antMatchers("/h2-console/**").permitAll()                    
                .anyRequest().authenticated()
                .and()
            .headers()
                .frameOptions().disable()
                .and()                
            .formLogin()
                .permitAll()
                .and()                
            .logout()
                .permitAll()
                .and()
            .csrf()
                .disable()
            .oauth2Login()
                .authorizationEndpoint()
                    .baseUri("/oauth2/authorize")
                    .authorizationRequestRepository(customAuthorizationRequestRepository)
                    .and()
                .redirectionEndpoint()
                    .baseUri("/oauth2/callback/*")
                    .and()
                .userInfoEndpoint()
                    // .userService(customOAuth2UserService)
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

    // WebSecurityConfigurerAdapter
}
