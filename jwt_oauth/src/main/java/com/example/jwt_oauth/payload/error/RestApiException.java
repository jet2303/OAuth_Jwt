package com.example.jwt_oauth.payload.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RestApiException extends RuntimeException{
    
    private final ErrorCode errorCode;
}
