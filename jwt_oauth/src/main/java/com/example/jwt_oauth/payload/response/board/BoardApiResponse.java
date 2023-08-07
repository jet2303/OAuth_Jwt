package com.example.jwt_oauth.payload.response.board;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.NotBlank;

import com.example.jwt_oauth.domain.board.BoardInfo;
import com.example.jwt_oauth.domain.board.BoardStatus;
import com.example.jwt_oauth.domain.board.FileInfo;
import com.example.jwt_oauth.domain.dto.BoardInfoDto;
import com.example.jwt_oauth.domain.dto.FileInfoDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class BoardApiResponse {
    
    // @NotBlank
    private Long id;

    @NotBlank
    private String email;

    @NotBlank
    private String userName;

    @NotBlank
    private String title;

    @NonNull
    private String content;

    @NotBlank
    private String boardStatus;

    @NotBlank
    private LocalDateTime createdDate;

    @NotBlank
    private String createBy;

    private LocalDateTime modifiedDate;

    private String modifiedBy;

    private List<FileInfo> fileList;

    public BoardApiResponse(BoardInfo boardInfo, List<FileInfo> fileList){
        this.id = boardInfo.getId();
        this.title = boardInfo.getTitle();
        this.content = boardInfo.getContent();
        this.boardStatus = boardInfo.getBoardStatus().toString();
        
        this.createBy = boardInfo.getCreatedBy();
        this.createdDate = boardInfo.getCreatedDate();
        this.modifiedBy = boardInfo.getModifiedBy();
        this.modifiedDate = boardInfo.getModifiedDate();

        this.fileList = fileList;
    }

    public BoardApiResponse(BoardInfo boardInfo){
        this.id = boardInfo.getId();
        this.email = boardInfo.getEmail();
        this.userName = boardInfo.getUserName();
        this.title = boardInfo.getTitle();
        this.content = boardInfo.getContent();
        this.boardStatus = boardInfo.getBoardStatus().toString();
        
        this.createBy = boardInfo.getCreatedBy();
        this.createdDate = boardInfo.getCreatedDate();
        this.modifiedBy = boardInfo.getModifiedBy();
        this.modifiedDate = boardInfo.getModifiedDate();
    }

    public BoardApiResponse(BoardInfoDto dto){
        this.id = dto.getId();
        this.email = dto.getEmail();
        this.userName = dto.getUserName();
        this.title = dto.getTitle();
        this.content = dto.getContent();
        this.boardStatus = dto.getBoardStatus().toString();

        this.fileList = toFileInfoList(dto.getFileInfoList());
        
        this.createBy = dto.getCreatedBy();
        this.createdDate = dto.getCreatedDate();
        this.modifiedBy = dto.getModifiedBy();
        this.modifiedDate = dto.getModifiedDate();
    }

    public static BoardApiResponse from(BoardInfoDto dto){
        // return new BoardApiResponse(dto.getId(), dto.getEmail(), dto.getUserName(),
        //                             dto.getTitle(), dto.getContent(), dto.getBoardStatus(), 
        //                             dto.getCreatedDate(), dto.getCreatedBy(), dto.getModifiedDate(), dto.getModifiedBy(), 
        //                             dto.getFileInfoList());
        return new BoardApiResponse(dto);
    }

    public static BoardApiResponse from(BoardInfo boardInfo){
        return new BoardApiResponse(boardInfo);
    }

    // FileInfo로 변환하는게 아닌 FileinfoDto로 넘길수있게 차후 수정필요.
    private List<FileInfo> toFileInfoList(List<FileInfoDto> dto){
        
        List<FileInfo> fileInfoList = dto.stream().map(element -> toFileInfo(element))
                                                    .collect(Collectors.toList());
        
        return fileInfoList;
    }

    private FileInfo toFileInfo(FileInfoDto dto){
        FileInfo fileInfo = FileInfo.builder()
                                    .fileName(dto.getFileName())
                                    .filePath(dto.getFilePath())
                                    .build();
        return fileInfo;
    }
}
