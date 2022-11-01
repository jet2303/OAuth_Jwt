package com.example.jwt_oauth.domain.user;


public enum UserEnum {
    
    Y("use", "계정 사용")
    ,N("not use", "계정 중지");

    private String status;
    private String description;

    private UserEnum(String status, String description){
        this.status = status;
        this.description = description;
    }

    public String getStatus(){
        return this.status;
    }

    public String getDescription(){
        return this.description;
    }
    
}
