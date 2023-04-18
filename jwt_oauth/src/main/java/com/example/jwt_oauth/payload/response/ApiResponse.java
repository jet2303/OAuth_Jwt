package com.example.jwt_oauth.payload.response;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ApiResponse<T> {
    
    private boolean check = false;

    private Object information;

    private T newInformation;

    public ApiResponse(){};

    @Builder
    public ApiResponse(boolean check, Object information, T newInformation){
        this.check = check;
        this.information = information;
        this.newInformation = newInformation;
    }
}
