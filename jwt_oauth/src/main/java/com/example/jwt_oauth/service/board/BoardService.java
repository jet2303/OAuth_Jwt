package com.example.jwt_oauth.service.board;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import com.example.jwt_oauth.repository.user.UserRepository;

import io.jsonwebtoken.lang.Strings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardService {

    
    private final BoardRepository boardRepository;
    private final FileInfoRepository fileInfoRepository;

    String filePath = "D:\\fastcampus\\97_OAuth_Jwt_board\\jwt_oauth\\src\\main\\resources\\static\\files";

    /**
    * @date : 2023-01-20
    * @author : AJS
    * @Description: 파라미터 boardInfo → BoardApiResponse로 변경
    **/

    private BoardInfo setBoardInfo(UserPrincipal userPrincipal, BoardInfo boardInfo, final List<MultipartFile> files){
        
        boardInfo.setBoardStatus(BoardStatus.REGISTERED);
        boardInfo.setEmail(userPrincipal.getEmail());
        boardInfo.setUserName(userPrincipal.getUserName());
        
        boardRepository.save(boardInfo);
        try{
            List<FileInfo> fileList = new ArrayList<>();

            if(files != null && emptyFileChk(files)){
                for (MultipartFile file : files) {
                    String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                    File saveFile = new File(filePath,fileName);
                    file.transferTo(saveFile);
                    FileInfo fileInfo = new FileInfo.FileInfoBuilder()
                                                    .fileName(fileName)
                                                    .filePath("/files/" + fileName)
                                                    .boardInfo(boardInfo)
                                                    .build();
                    fileList.add(fileInfo);
                }
                
                fileInfoRepository.saveAll(fileList);    
            }
            boardInfo.setFileInfoList(fileList);
        }catch(Exception e){
            e.printStackTrace();
        }
        return boardInfo;
    }

    public Header<BoardApiResponse> create(final CreateBoardRequest request, final List<MultipartFile> files
                                            , UserPrincipal userPrincipal){
        BoardInfo boardInfo = requestToBoard(request, userPrincipal);        

        BoardInfo boardSaved = setBoardInfo(userPrincipal, boardInfo, files);

        if(!files.isEmpty()){
            //boardapiresponse builder로 바꿀수있진않나??
            return Header.OK(new BoardApiResponse(boardSaved, boardSaved.getFileInfoList()));

        }else{
            return Header.OK(new BoardApiResponse(boardSaved));
        }

    }
   

    public Header<BoardApiResponse> read(final Long id){
        BoardInfo boardInfo = boardRepository.findById(id).orElseThrow(() -> new NoSuchElementException("not found board"));
        
        
        return Header.OK(BoardApiResponse.builder()
                                            .id(boardInfo.getId())
                                            .userName(boardInfo.getUserName())
                                            .title(boardInfo.getTitle())
                                            .content(boardInfo.getContent())
                                            .boardStatus(boardInfo.getBoardStatus())
                                            .createdDate(boardInfo.getCreatedDate())
                                            .createBy(boardInfo.getCreatedBy())
                                            .modifiedDate(boardInfo.getModifiedDate())
                                            .modifiedBy(boardInfo.getModifiedBy())
                                            .fileList(getOriginFileName(boardInfo.getFileInfoList()))
                                            .build());
    }

    public Header<List<BoardApiResponse>> getBoardList(){
        
        List<Boardlist> boardList = boardRepository.findByBoardStatus(BoardStatus.REGISTERED)
                                                    .orElseThrow( () -> new RuntimeException("등록된 게시글이 없습니다."));

        List<BoardApiResponse> boardApiResponses = new ArrayList<>();
        if(boardList.size()<1){
            return Header.OK(boardApiResponses);
        }        
        
        boardApiResponses = boardList.stream().map(board -> response(board))
                                                 .collect(Collectors.toList());

        return Header.OK(boardApiResponses);
    }
    
    // 권한 여부에 따른 수정 유무 로직 추가할것.
    @Transactional
    public Header<BoardApiResponse> update(final CreateBoardRequest request, final List<MultipartFile> files, final Long id
                                                ,UserPrincipal userPrincipal){

        //// boardInfo 수정 메소드
        BoardInfo boardInfo = boardRepository.findById(id).get();
        List<FileInfo> oldFileList = fileInfoRepository.findByBoardInfo(boardInfo).get();

        boardInfo.setTitle(request.getTitle());
        boardInfo.setContent(request.getContent());
        boardInfo.getFileInfoList().clear();

        if(emptyFileChk(files)){
            //// 수정필요
            fileInfoRepository.deleteByBoardInfo(boardInfo);
            
            List<FileInfo> fileList = files.stream()
                                            .map(file -> multiPartFileToFileInfo(file, boardInfo))
                                            .collect(Collectors.toList());
            
            ////boardInfo.setFileList 이런식으로 추가하는 방법 > 연관관계 수정       
            fileInfoRepository.saveAll(fileList);
            
            // BoardInfo updateBoard = requestToBoard(request, userPrincipal);
            
        }else{
            boardInfo.setFileInfoList(null);
        }

        return Header.OK(BoardApiResponse.builder()
                                            .id(boardInfo.getId())
                                            .email(boardInfo.getEmail())
                                            .userName(boardInfo.getUserName())
                                            .title(boardInfo.getTitle())
                                            .content(boardInfo.getContent())
                                            .boardStatus(boardInfo.getBoardStatus())
                                            .createdDate(boardInfo.getCreatedDate())
                                            .createBy(boardInfo.getCreatedBy())
                                            .modifiedDate(boardInfo.getModifiedDate())
                                            .modifiedBy(boardInfo.getModifiedBy())
                                            .fileList(getOriginFileName(boardInfo.getFileInfoList()))
                                            .build());
    }

    
    // public Header<ApiResponse> delete(UserPrincipal userPrincipal, final Long id){
    @Transactional
    public void delete(UserPrincipal userPrincipal, final Long id){
        
        BoardInfo boardInfo = boardRepository.findById(id).get();
        
        if(userPrincipal.getAuthorities().equals("ROLE_ADMIN") || userPrincipal.getUserName().equals(boardInfo.getUserName())){
            
            // delete 로 게시글 비활성화
            boardInfo.setBoardStatus(BoardStatus.UNREGISTERED);

            // boardRepository.save(boardInfo);

            // return Header.OK(new BoardApiResponse(boardInfo));
        }
        // return Header.OK(new BoardApiResponse(boardInfo));

        // 게시글을 작성한 본인, 관리자 제외 삭제 불가.
        // boardInfo에 있는 useremail, 현재 로그인한 useremail 비교
        // if(userPrincipal.getAuthorities().equals("ROLE_ADMIN") || userPrincipal.getUserName().equals(boardInfo.getUserName())){
            
        //     // delete 로 게시글 비활성화
            
        //     boardInfo.setBoardStatus(BoardStatus.UNREGISTERED);
        //     return Header.OK(ApiResponse.builder()
        //                             .check(true)
        //                             .newInformation(Message.builder()
        //                                                     .message("success delete")
        //                                                     .build())
        //                             .build());
        // }
        // return Header.OK(ApiResponse.builder()
        //                             .check(true)
        //                             .newInformation(Message.builder()
        //                                                     .message("failed delete")
        //                                                     .build())
        //                             .build());
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

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////

    private BoardApiResponse response(Boardlist boardList){
        BoardApiResponse response = BoardApiResponse.builder()
                                                        .id(boardList.getId())
                                                        .email(boardList.getEmail())
                                                        .userName(boardList.getUserName())
                                                        .title(boardList.getTitle())
                                                        .content(boardList.getContent())
                                                        .createdDate(boardList.getCreatedDate())
                                                        .createBy(boardList.getCreatedBy())
                                                        .modifiedDate(boardList.getModifiedDate())
                                                        .modifiedBy(boardList.getModifiedBy())
                                                        .fileList(boardList.getFileInfoList())
                                                        .build();
        return response;                                                        
    }


    private BoardInfo requestToBoard(CreateBoardRequest request, UserPrincipal userPrincipal){
        BoardInfo boardInfo = new BoardInfo.BoardInfoBuilder()
                                            .email(userPrincipal.getEmail())
                                            .userName(request.getUserName())
                                            .title(request.getTitle())
                                            .content(request.getContent())
                                            .boardStatus(request.getBoardStatus())
                                            .build();
        return boardInfo;
    }

    private boolean emptyFileChk(List<MultipartFile> files){
        
        for (MultipartFile file : files) {

            if("".equals(file.getOriginalFilename())){
                return false;
            }
        }

        return true;
    }

    private static List<FileInfo> getOriginFileName(List<FileInfo> fileList){
        // cf837476-a611-4274-a469-02569f7cb97d_GPU.PNG
        
        String originFileName;
        String[] arr;
        for (FileInfo fileInfo : fileList) {
            arr = fileInfo.getFileName().split("_");
            originFileName=arr[1];
            fileInfo.setFileName(originFileName);
        }
        return fileList;
    }

    private FileInfo multiPartFileToFileInfo(MultipartFile file, BoardInfo boardInfo){
        try{
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            File saveFile = new File(filePath,fileName);
            file.transferTo(saveFile);
            FileInfo fileInfo = new FileInfo.FileInfoBuilder()
                                            .fileName(fileName)
                                            .filePath("/files/" + fileName)
                                            .boardInfo(boardInfo)
                                            .build();
            return fileInfo;
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
        
        return null;
    }


}
