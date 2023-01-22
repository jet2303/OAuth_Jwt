package com.example.jwt_oauth.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class Pagenation{

    private int totalPages;

    private long totalElements;

    private int currentPage;

    private int currentElements;

    //추후에 빌더 static 클래스 만들어놓기
}