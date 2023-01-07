package com.example.jwt_oauth.service.board;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.jwt_oauth.config.security.token.CurrentUser;
import com.example.jwt_oauth.config.security.token.UserPrincipal;
import com.example.jwt_oauth.domain.board.BoardInfo;
import com.example.jwt_oauth.domain.board.BoardStatus;
import com.example.jwt_oauth.domain.board.FileInfo;
import com.example.jwt_oauth.domain.dto.BoardInfoDto;
import com.example.jwt_oauth.domain.dto.BoardInfoDto.BoardInfoDtoBuilder;
import com.example.jwt_oauth.payload.response.ApiResponse;
import com.example.jwt_oauth.payload.response.Message;
import com.example.jwt_oauth.repository.board.BoardRepository;
import com.example.jwt_oauth.repository.board.FileInfoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardService {

    private final BoardRepository boardRepository;
    private final FileInfoRepository fileInfoRepository;

     String filePath = "D:\\fastcampus\\97_Oauth2_jwt\\jwt_oauth\\src\\main\\resources\\static\\files";
     
    public ResponseEntity<?> create(@CurrentUser UserPrincipal userPrincipal, BoardInfoDto boardInfoDto
                                    ,List<MultipartFile> files){
        
        try{
            BoardInfo boardInfo = dtoToBoard(boardInfoDto);
            BoardInfo boardSaved = boardRepository.save(boardInfo);

            log.info("{}",boardSaved);
            
            if(files != null){
                List<FileInfo> fileList = new ArrayList<>();
                for (MultipartFile file : files) {
                    String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                    File saveFile = new File(filePath,fileName);
                    file.transferTo(saveFile);
                    FileInfo fileInfo = new FileInfo.FileInfoBuilder()
                                                    .fileName(fileName)
                                                    .filePath("/files/" + fileName)
                                                    .boardInfo(boardSaved)
                                                    .build();
                    fileList.add(fileInfo);
                }
                
                fileInfoRepository.saveAll(fileList);
            }
                
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return ResponseEntity.ok(ApiResponse.builder()
                                                .check(true)
                                                .information(Message.builder()
                                                                        .message("create success")
                                                                        .build())
                                                .build());
    }

    public ResponseEntity<?> read(@CurrentUser UserPrincipal userPrincipal){

        return ResponseEntity.ok(ApiResponse.builder()
                                                .check(false)
                                                .information(Message.builder()
                                                                        .message("fail")
                                                                        .build())
                                                .build());
    }

    public ResponseEntity<?> update(@CurrentUser UserPrincipal userPrincipal){

        return ResponseEntity.ok(ApiResponse.builder()
                                                .check(false)
                                                .information(Message.builder()
                                                                        .message("fail")
                                                                        .build())
                                                .build());
    }

    public ResponseEntity<?> delete(@CurrentUser UserPrincipal userPrincipal){

        return ResponseEntity.ok(ApiResponse.builder()
                                                .check(false)
                                                .information(Message.builder()
                                                                        .message("fail")
                                                                        .build())
                                                .build());
    }

    public ResponseEntity<?> getList(@CurrentUser UserPrincipal userPrincipal){

        List<BoardInfo> boardInfos = boardRepository.findAll();


        return ResponseEntity.ok(ApiResponse.builder()
                                                .check(false)
                                                .information(boardInfos)
                                                .build());
    }

    private BoardInfoDto boardToDto(BoardInfo boardInfo){
        BoardInfoDto dto = new BoardInfoDtoBuilder().title(boardInfo.getTitle())
                                                    .content(boardInfo.getContent())
                                                    .boardStatus(boardInfo.getBoardStatus().toString())
                                                    // .fileName(boardInfo.)
                                                    .build();
        return dto;
    }

    private BoardInfo dtoToBoard(BoardInfoDto dto){
        BoardInfo boardInfo = new BoardInfo.BoardInfoBuilder()
                                            .title(dto.getTitle())
                                            .content(dto.getContent())
                                            .boardStatus(BoardStatus.valueOf(dto.getBoardStatus()))
                                            .build();
                                            
        return boardInfo;
    }

    private FileInfo dtoToFile(BoardInfoDto dto){
        FileInfo fileInfo = new FileInfo.FileInfoBuilder()
                                        .fileName(dto.getFileName())
                                        .filePath(dto.getFilePath())
                                        .build();
        return fileInfo;                                       
    }
}
