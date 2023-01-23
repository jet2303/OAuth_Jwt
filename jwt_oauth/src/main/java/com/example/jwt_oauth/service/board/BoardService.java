package com.example.jwt_oauth.service.board;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.jwt_oauth.config.security.token.CurrentUser;
import com.example.jwt_oauth.config.security.token.UserPrincipal;
import com.example.jwt_oauth.domain.board.BoardInfo;
import com.example.jwt_oauth.domain.board.BoardStatus;
import com.example.jwt_oauth.domain.board.FileInfo;
import com.example.jwt_oauth.domain.dto.BoardInfoDto;
import com.example.jwt_oauth.domain.dto.BoardInfoDto.BoardInfoDtoBuilder;
import com.example.jwt_oauth.payload.Header;
import com.example.jwt_oauth.payload.Pagenation;
import com.example.jwt_oauth.payload.request.board.CreateBoardRequest;
import com.example.jwt_oauth.payload.response.ApiResponse;
import com.example.jwt_oauth.payload.response.Message;
import com.example.jwt_oauth.payload.response.board.BoardApiResponse;
import com.example.jwt_oauth.repository.board.BoardRepository;
import com.example.jwt_oauth.repository.board.FileInfoRepository;
import com.example.jwt_oauth.repository.board.projection.Boardlist;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardService {

    private final BoardRepository boardRepository;
    private final FileInfoRepository fileInfoRepository;

     String filePath = "D:\\fastcampus\\97_Oauth2_jwt\\jwt_oauth\\src\\main\\resources\\static\\files";

    /**
    * @date : 2023-01-20
    * @author : AJS
    * @Description: 파라미터 boardInfo → BoardApiResponse로 변경
    **/
    public Header<BoardApiResponse> create(final CreateBoardRequest request, final List<MultipartFile> files
                                            , UserPrincipal userPrincipal){
        BoardInfo boardInfo = requestToBoard(request);
        
        // BoardInfo boardSaved = boardRepository.save(boardInfo);

        List<FileInfo> fileList = new ArrayList<>();
        try{
        
            if(files != null){
                for (MultipartFile file : files) {
                    String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                    File saveFile = new File(filePath,fileName);
                    file.transferTo(saveFile);
                    FileInfo fileInfo = new FileInfo.FileInfoBuilder()
                                                    .fileName(fileName)
                                                    .filePath("/files/" + fileName)
                                                    // .boardInfo(boardSaved)
                                                    .build();
                    fileList.add(fileInfo);
                }
                
                fileInfoRepository.saveAll(fileList);
            }
                
        }catch(Exception e){
            e.printStackTrace();
        }
        boardInfo.setFileInfoList(fileList);
        boardInfo.setBoardStatus(BoardStatus.REGISTERED);
        boardInfo.setUserName(userPrincipal.getUserName());
        

        BoardInfo boardSaved = boardRepository.save(boardInfo);
        
        // log.info("findall : {} , {}", boardSaved.getCreatedDate(), boardSaved.getModifiedDate());
        // log.info("{}", boardRepository.findById(1L).get()); 
        return Header.OK(new BoardApiResponse(boardSaved, fileList));
    }
   

    public Header<BoardApiResponse> read(final Long id){

        BoardInfo boardInfo = boardRepository.findById(id).get();
        // return Header.ERROR();

        return Header.OK(BoardApiResponse.builder()
                                            .id(boardInfo.getId())
                                            .userName(boardInfo.getUserName())
                                            .title(boardInfo.getTitle())
                                            .content(boardInfo.getContent())
                                            .boardStatus(boardInfo.getBoardStatus())
                                            .createdDate(boardInfo.getCreatedDate())
                                            // .createBy(boardInfo.getCreatedBy())
                                            .modifiedDate(boardInfo.getModifiedDate())
                                            // .modifiedBy(boardInfo.getModifiedBy())
                                            .fileList(boardInfo.getFileInfoList())
                                            .build());
        // return Header.OK(new BoardApiResponse(boardInfo, boardInfo.getFileInfoList()));

    }

    // public Header<List<BoardApiResponse>> getBoardList(){
    //     List<BoardApiResponse> boardApiResponses = new ArrayList<>();
    //     List<Boardlist> boardList = boardRepository.findByBoardStatus(BoardStatus.REGISTERED).get();
    //     for (Boardlist board : boardList) {
    //         BoardApiResponse newBoard = BoardApiResponse.builder()
    //                                                         .id(board.getId())
    //                                                         .title(board.getTitle())
    //                                                         .createdDate(board.getCreatedDate())
    //                                                         .build();
    //         boardApiResponses.add(newBoard);
    //     }
        

    //     return Header.OK(boardApiResponses);
    // }

    

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

    public Header<List<BoardApiResponse>> getList(){
        // List<Board> boardList = boardRepository.findAll();
        List<Boardlist> boardList = boardRepository.findByBoardStatus(BoardStatus.REGISTERED)
                                                    .orElseThrow( () -> new RuntimeException("등록된 게시글이 없습니다."));

        if(boardList.size()<1){
            // return new RuntimeException();
        }
        List<BoardApiResponse> boardApiResponses = new ArrayList<>();

        boardList.stream().map( board-> boardApiResponses.add(response(board)));

        return Header.OK(boardApiResponses);
    }

    public Header<List<BoardApiResponse>> search(Pageable pageable){
        
        Page<Boardlist> boards = boardRepository.findByBoardStatus(BoardStatus.REGISTERED, pageable)
                                                .orElseThrow(() -> new RuntimeException("등록된 게시글이 없습니다."));
                                            
                                            
        List<BoardApiResponse> boardApiResponses = boards.stream().map(board -> response(board))
                                                                    .collect(Collectors.toList());
        

        Pagenation pagenation = Pagenation.builder()
                                            .totalPages(boards.getTotalPages())
                                            .totalElements(boards.getTotalElements())
                                            .currentPage(boards.getNumber())
                                            .currentElements(boards.getNumberOfElements())
                                            .build();

        return Header.OK(boardApiResponses, pagenation);
    }

    private BoardApiResponse response(Boardlist boardList){
        BoardApiResponse response = BoardApiResponse.builder()
                                                        .id(boardList.getId())
                                                        .title(boardList.getTitle())
                                                        .content(boardList.getContent())
                                                        .createdDate(boardList.getCreatedDate())
                                                        .userName(boardList.getUserName())
                                                        .build();
        return response;                                                        
    }


    private BoardInfo requestToBoard(CreateBoardRequest request){
        BoardInfo boardInfo = new BoardInfo.BoardInfoBuilder()
                                            .userName(request.getUserName())
                                            .title(request.getTitle())
                                            .content(request.getContent())
                                            .boardStatus(request.getBoardStatus())
                                            .build();
        return boardInfo;
    }



    // private BoardInfoDto boardToDto(BoardInfo boardInfo){
    //     BoardInfoDto dto = new BoardInfoDtoBuilder().title(boardInfo.getTitle())
    //                                                 .content(boardInfo.getContent())
    //                                                 .boardStatus(boardInfo.getBoardStatus().toString())
    //                                                 // .fileName(boardInfo.)
    //                                                 .build();
    //     return dto;
    // }

    // private FileInfo dtoToFile(BoardInfoDto dto){
    //     FileInfo fileInfo = new FileInfo.FileInfoBuilder()
    //                                     .fileName(dto.getFileName())
    //                                     .filePath(dto.getFilePath())
    //                                     .build();
    //     return fileInfo;                                       
    // }
}
