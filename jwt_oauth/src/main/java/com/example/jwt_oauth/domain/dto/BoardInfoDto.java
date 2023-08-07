package com.example.jwt_oauth.domain.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.NotBlank;

import com.example.jwt_oauth.domain.board.BoardInfo;
import com.example.jwt_oauth.domain.board.BoardStatus;
// import com.example.jwt_oauth.domain.dto.FileInfoDto;
import com.example.jwt_oauth.domain.board.FileInfo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@AllArgsConstructor
@ToString
@Accessors(chain = true)
public class BoardInfoDto {

    private Long id;

    @NotBlank
    private String email;

    @NotBlank
    private String userName;

    @NotBlank
    private String title;

    
    private String content;

    @NotBlank
    private BoardStatus boardStatus;


    //filename, filePath 제거할것.
    
    private String fileName;

    
    private String filePath;

    // 23.07.05 AJS 
    // Lazy Loading 오류 수정을 위해 DTO 추가
    // 추가 후 filename, filepath 필드 제거 필요.
    
    private List<FileInfoDto> fileInfoList;
    
    @NotBlank
    private LocalDateTime createdDate;

    @NotBlank
    private String createdBy;

    private LocalDateTime modifiedDate;

    private String modifiedBy;

    public static class BoardInfoDtoBuilder{

        private Long id;

        private String email;

        private String userName;

        private String title;

        private String content;
    
        private BoardStatus boardStatus;
    
        //fileName, filePath 제거할것.
        private String fileName;
    
        private String filePath;

        private List<FileInfoDto> fileInfoList;

        private LocalDateTime createdDate;

        private String createdBy;

        private LocalDateTime modifiedDate;

        private String modifiedBy;

        public BoardInfoDtoBuilder id(Long id){
            this.id = id;
            return this;
        }

        public BoardInfoDtoBuilder email(String email){
            this.email = email;
            return this;
        }

        public BoardInfoDtoBuilder userName(String userName){
            this.userName = userName;
            return this;
        }

        public BoardInfoDtoBuilder title(String title){
            this.title = title;
            return this;
        }

        public BoardInfoDtoBuilder content(String content){
            this.content = content;
            return this;
        }

        public BoardInfoDtoBuilder boardStatus(BoardStatus boardStatus){
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

        public BoardInfoDtoBuilder fileInfoList(List<FileInfoDto> fileInfoList){
            this.fileInfoList = fileInfoList;
            return this;
        } 
        
        public BoardInfoDtoBuilder createdDate(LocalDateTime createdDate){
            this.createdDate = createdDate;
            return this;
        } 

        public BoardInfoDtoBuilder createdBy(String createdBy){
            this.createdBy = createdBy;
            return this;
        } 

        public BoardInfoDtoBuilder modifiedDate(LocalDateTime modifiedDate){
            this.modifiedDate = modifiedDate;
            return this;
        } 

        public BoardInfoDtoBuilder modifiedBy(String modifiedBy){
            this.modifiedBy = modifiedBy;
            return this;
        } 
        
        public BoardInfoDto build(){
            return new BoardInfoDto(this);
        }
    }

    public BoardInfoDto(BoardInfoDtoBuilder boardInfoDtoBuilder){
        this.id = boardInfoDtoBuilder.id;
        this.email = boardInfoDtoBuilder.email;
        this.userName = boardInfoDtoBuilder.userName;
        
        this.title = boardInfoDtoBuilder.title;
        this.content = boardInfoDtoBuilder.content;
        this.boardStatus = boardInfoDtoBuilder.boardStatus;
        //fileName filePath 제거
        this.fileName = boardInfoDtoBuilder.fileName;
        this.filePath = boardInfoDtoBuilder.filePath;
        this.fileInfoList = boardInfoDtoBuilder.fileInfoList;

        this.createdDate = boardInfoDtoBuilder.createdDate;
        this.createdBy = boardInfoDtoBuilder.createdBy;
        this.modifiedDate = boardInfoDtoBuilder.modifiedDate;
        this.modifiedBy = boardInfoDtoBuilder.modifiedBy;
    }

    public BoardInfoDto(){}

    public BoardInfoDto(Long id, String email, String userName, String title, String content, BoardStatus boardStatus, List<FileInfoDto> fileInfoList
                        ,LocalDateTime createdDate, String createdBy){
        this.id = id;
        this.email = email;
        this.userName = userName;
        this.title = title;
        this.content = content;
        this.boardStatus = boardStatus;
        this.fileInfoList = fileInfoList;
        this.createdDate = createdDate;
        this.createdBy = createdBy;
    }
    public BoardInfoDto(BoardInfo boardInfo){
        
        this.id = boardInfo.getId();
        this.email = boardInfo.getEmail();
        this.userName = boardInfo.getUserName();
        this.title = boardInfo.getTitle();
        this.content = boardInfo.getContent();
        this.boardStatus = boardInfo.getBoardStatus();
        this.fileInfoList = boardInfo.getFileInfoList().stream()
                                                        .map(file -> new FileInfoDto(file))
                                                        .collect(Collectors.toList());

    }

}
