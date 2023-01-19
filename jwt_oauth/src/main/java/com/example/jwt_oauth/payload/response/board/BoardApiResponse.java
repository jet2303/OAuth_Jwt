package com.example.jwt_oauth.payload.response.board;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.constraints.NotBlank;

import com.example.jwt_oauth.domain.board.BoardInfo;
import com.example.jwt_oauth.domain.board.BoardStatus;
import com.example.jwt_oauth.domain.board.FileInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder
@AllArgsConstructor
@Getter
public class BoardApiResponse {
    
    @NotBlank
    private Long id;

    @NotBlank
    private String title;

    // 작성자 추가
    // @NotBlank
    // private String name;

    @NonNull
    private String content;

    @NotBlank
    private BoardStatus boardStatus;

    private LocalDateTime createdDate;

    private String createBy;

    private LocalDateTime modifiedDate;

    private String modifiedBy;

    private List<FileInfo> fileList;

    public BoardApiResponse(BoardInfo boardInfo, List<FileInfo> fileList){
        this.id = boardInfo.getId();
        this.title = boardInfo.getTitle();
        this.content = boardInfo.getContent();
        this.boardStatus = boardInfo.getBoardStatus();
        
        this.createBy = boardInfo.getCreatedBy();
        this.createdDate = boardInfo.getCreatedDate();
        this.modifiedBy = boardInfo.getModifiedBy();
        this.modifiedDate = boardInfo.getModifiedDate();

        this.fileList = fileList;
    }
}
