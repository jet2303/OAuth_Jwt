package com.example.jwt_oauth.domain.dto;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class BoardInfoDto {

    @NotBlank
    private String title;

    @NonNull
    private String content;

    @NotBlank
    private String boardStatus;

    @NonNull
    private String fileName;

    @NonNull
    private String filePath;
    
    public static class BoardInfoDtoBuilder{

        private String title;

        private String content;
    
        private String boardStatus;
    
        private String fileName;
    
        private String filePath;

        public BoardInfoDtoBuilder title(String title){
            this.title = title;
            return this;
        }

        public BoardInfoDtoBuilder content(String content){
            this.content = content;
            return this;
        }

        public BoardInfoDtoBuilder boardStatus(String boardStatus){
            this.boardStatus = boardStatus;
            return this;
        }

        public BoardInfoDtoBuilder fileName(String fileName){
            this.fileName = fileName;
            return this;
        }

        public BoardInfoDtoBuilder filePath(String filePath){
            this.filePath = filePath;
            return this;
        }   
        
        public BoardInfoDto build(){
            return new BoardInfoDto(this);
        }
    }

    public BoardInfoDto(BoardInfoDtoBuilder boardInfoDtoBuilder){
        this.title = boardInfoDtoBuilder.title;
        this.content = boardInfoDtoBuilder.content;
        this.boardStatus = boardInfoDtoBuilder.boardStatus;
        this.fileName = boardInfoDtoBuilder.fileName;
        this.filePath = boardInfoDtoBuilder.filePath;
    }

    public BoardInfoDto(){}
}
