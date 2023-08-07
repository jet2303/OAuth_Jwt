package com.example.jwt_oauth.controller;


import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.jwt_oauth.domain.user.User;
import com.example.jwt_oauth.payload.error.ErrorCode;
import com.example.jwt_oauth.payload.error.RestApiException;
import com.example.jwt_oauth.payload.error.errorCodes.UserErrorCode;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class GlobalExceptionHandler {
    

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUser(){
      throw new RestApiException(UserErrorCode.CUSTOM_ERROR);
      
    }

}
