package com.example.jwt_oauth.payload;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@SuppressWarnings("unchecked")
public class Header<T> {
    private LocalDateTime transactionTime;         // ISO format,  YYYY-MM-DD HH:mm:ss

    //api 응답 코드
    private String resultCode;

    //api 부가 설명
    private String description;

    //generic으로
    private T data;

    private Pagenation pagenation;    

    public static <T> Header<T> OK(){
        
        return (Header<T>) Header.builder()
                                    .transactionTime(LocalDateTime.now())
                                    .resultCode("OK")
                                    .description("OK")
                                    .build();
    }

    public static <T> Header<T> OK(T data){
        return (Header<T>) Header.builder()
                                    .transactionTime(LocalDateTime.now())
                                    .resultCode("OK")
                                    .description("OK")
                                    .data(data)
                                    .build();
    }

    public static <T> Header<T> ERROR(String description){
        return (Header<T>) Header.builder()
                                    .transactionTime(LocalDateTime.now())
                                    .resultCode("ERROR")
                                    .description(description)
                                    .build();
    }

    //DATA OK(메소드 오버로딩 pagenation)
    public static <T> Header<T> OK(T data, Pagenation pagenation){                 
        return (Header<T>) Header.builder()
                .transactionTime(LocalDateTime.now())
                .resultCode("OK")
                .description("OK")
                .data(data)
                .pagenation(pagenation)
                .build();
    }
}
