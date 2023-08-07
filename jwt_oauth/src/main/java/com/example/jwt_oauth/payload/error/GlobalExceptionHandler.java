package com.example.jwt_oauth.payload.error;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.example.jwt_oauth.payload.error.errorCodes.CommonErrorCode;
import com.example.jwt_oauth.payload.error.errorCodes.UserErrorCode;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public abstract class GlobalExceptionHandler extends ResponseEntityExceptionHandler{

    @ExceptionHandler(RestApiException.class)
    public ResponseEntity<Object> handleCustomException(RestApiException e){
        ErrorCode errorCode = e.getErrorCode();
        return handleExceptionInternal(errorCode);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalargument(IllegalArgumentException e){
        ErrorCode errorCode = CommonErrorCode.INVALID_PARAMETER;
        return handleExceptionInternal(errorCode, e.getMessage());
    }

    @ExceptionHandler(value = {CustomException.class})
    public ResponseEntity<Object> noSuchElementException(NoSuchElementException e){
        ErrorCode errorCode = UserErrorCode.CUSTOM_ERROR;
        return handleExceptionInternal(errorCode);
    }

////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        
        ErrorCode errorCode = CommonErrorCode.INVALID_PARAMETER;
        return handleExceptionInternal(ex, errorCode);
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<Object> handleAllException(Exception ex){
        ErrorCode errorCode = CommonErrorCode.INTERNAL_SERVER_ERROR;
        return handleExceptionInternal(errorCode);
    }

///////////////////////////////////////////////////////////////////////////////////////////
    private ResponseEntity<Object> handleExceptionInternal(ErrorCode errorCode){
        return ResponseEntity.status(errorCode.getHttpStatus())
                                .body(makeErrorResponse(errorCode));
    }

    private ResponseEntity<Object> handleExceptionInternal(BindException e, ErrorCode errorCode){
        return ResponseEntity.status(errorCode.getHttpStatus())
                                .body(makeErrorResponse(e, errorCode));
    }

    private ResponseEntity<Object> handleExceptionInternal(ErrorCode errorCode, String message) {
        return ResponseEntity.status(errorCode.getHttpStatus())
                             .body(makeErrorResponse(errorCode, message));
    }

////////////////////////////////////////////////////////////////////////////////////////////
    private ErrorResponse makeErrorResponse(ErrorCode errorCode){
        return ErrorResponse.builder()
                            .code(errorCode.name())
                            .message(errorCode.getMessage())
                            .build();
    }

    private ErrorResponse makeErrorResponse(ErrorCode errorCode, String message) {
        return ErrorResponse.builder()
                            .code(errorCode.name())
                            .message(message)
                            .build();
    }
    
    private ErrorResponse makeErrorResponse(BindException e, ErrorCode errorCode){
        List<ErrorResponse.ValidationError> validationErrorList = e.getBindingResult()
                                                                    .getFieldErrors()
                                                                    .stream().map(ErrorResponse.ValidationError::of)
                                                                                .collect(Collectors.toList());
        return ErrorResponse.builder()
                            .code(errorCode.name())
                            .message(errorCode.getMessage())
                            .errors(validationErrorList)
                            .build();
    }

    

    
}
