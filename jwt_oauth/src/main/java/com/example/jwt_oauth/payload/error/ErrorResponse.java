package com.example.jwt_oauth.payload.error;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.validation.FieldError;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class ErrorResponse {
    
    private final String code;
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final List<ValidationError> errors;

    
    @Getter
    @Builder
    @RequiredArgsConstructor
    public static class ValidationError{
        private final String field;
        private final String message;
        
        public static ValidationError of(final FieldError fieldError){
            return ValidationError.builder()
                                    .field(fieldError.getField())
                                    .message(fieldError.getDefaultMessage())
                                    .build();
        }
    }
}
