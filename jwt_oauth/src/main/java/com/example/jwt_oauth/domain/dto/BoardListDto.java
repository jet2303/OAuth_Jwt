package com.example.jwt_oauth.domain.dto;


import java.time.LocalDateTime;
import java.util.List;

import com.example.jwt_oauth.domain.board.FileInfo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BoardListDto {

    private Long id;

    private String email;

    private String userName;

    private String title;

    private String content;

    private LocalDateTime createdDate;

    private String createdBy;

    private LocalDateTime modifiedDate;

    private String modifiedBy;

}
