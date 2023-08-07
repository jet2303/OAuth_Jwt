package com.example.jwt_oauth.payload.error;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
    
    String name();
    HttpStatus getHttpStatus();
    String getMessage();
}
