package com.example.jwt_oauth.payload.error.errorCodes;

import org.springframework.http.HttpStatus;

import com.example.jwt_oauth.payload.error.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BoardErrorCode implements ErrorCode{
    
    CREATE_EMPTY_TITLE(HttpStatus.INTERNAL_SERVER_ERROR, "Empty Title.")
    ,CREATE_EMPTY_CONTENT(HttpStatus.INTERNAL_SERVER_ERROR, "Empty Content.")
    ,NOT_FOUND_BOARDLIST(HttpStatus.INTERNAL_SERVER_ERROR, "Not Found BoardList.")
    ,NOT_FOUND_BOARD(HttpStatus.INTERNAL_SERVER_ERROR, "Not Found Board.")
    ;

    private final HttpStatus httpStatus;
    private final String Message;
}
