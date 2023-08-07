package com.example.jwt_oauth.payload.error.errorCodes;

import org.springframework.http.HttpStatus;

import com.example.jwt_oauth.payload.error.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode{

    INACTIVE_USER(HttpStatus.FORBIDDEN, "user is inactive")
    ,NOT_FOUND_USER(HttpStatus.FORBIDDEN, "not found user")
    ,NOT_MATCHED_PASSWORD(HttpStatus.FORBIDDEN, "not matched password")
    ,ANONYMOUS_USER(HttpStatus.FORBIDDEN, "Anonymous don't make a board")
    ,CUSTOM_ERROR(HttpStatus.SEE_OTHER, "Custom Error")
    ,USER_ALREADY_EXISTS(HttpStatus.FORBIDDEN, "user is already exists")
    ;

    private final HttpStatus httpStatus;
    private final String message;
    
}
