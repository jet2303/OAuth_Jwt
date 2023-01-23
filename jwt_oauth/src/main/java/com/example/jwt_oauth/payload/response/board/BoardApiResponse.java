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
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BoardApiResponse {
    
    @NotBlank
    private Long id;

    @NotBlank
    private String title;

    @NotBlank
    private String userName;

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
        
        // this.createBy = boardInfo.getCreatedBy();
        this.createdDate = boardInfo.getCreatedDate();
        // this.modifiedBy = boardInfo.getModifiedBy();
        this.modifiedDate = boardInfo.getModifiedDate();

        this.fileList = fileList;
    }
}