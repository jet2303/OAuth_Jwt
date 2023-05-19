package com.example.jwt_oauth.config;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;


import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import lombok.extern.slf4j.Slf4j;

@Component
public class AuditorAwareImpl implements AuditorAware<String>{

    @Override
    public Optional<String> getCurrentAuditor() {
        // HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
        // request.getSession()
        return Optional.of("TestUser");
    }
    
}
