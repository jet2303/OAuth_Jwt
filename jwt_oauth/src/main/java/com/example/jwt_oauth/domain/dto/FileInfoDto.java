package com.example.jwt_oauth.domain.dto;


import com.example.jwt_oauth.domain.board.FileInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
public class FileInfoDto {
    
    private Long id;

    private String fileName;

    private String filePath;

    public FileInfoDto(FileInfo fileInfo){
        this.id = fileInfo.getId();
        this.fileName = fileInfo.getFileName();
        this.filePath = fileInfo.getFilePath();
    }
}
