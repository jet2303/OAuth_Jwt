package com.example.jwt_oauth.domain.board;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BoardStatus {
    
    REGISTERED("등록됨")
    ,UNREGISTERED("등록안됨");

    private String description;
}
