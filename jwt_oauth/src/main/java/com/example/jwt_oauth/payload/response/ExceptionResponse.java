package com.example.jwt_oauth.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ExceptionResponse {
    
    private int httpValue;
    private String description;

    public ExceptionResponse(int httpValue, String description){
        this.httpValue = httpValue;
        this.description = description;
    }

    public int getHttpValue() {
        return httpValue;
    }

    public String getDescription() {
        return description;
    }

    
}
