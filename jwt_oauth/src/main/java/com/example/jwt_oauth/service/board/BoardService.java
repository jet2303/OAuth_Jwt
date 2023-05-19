package com.example.jwt_oauth.service.board;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.jwt_oauth.config.security.token.CurrentUser;
import com.example.jwt_oauth.config.security.token.UserPrincipal;
import com.example.jwt_oauth.domain.board.BoardInfo;
import com.example.jwt_oauth.domain.board.BoardStatus;
import com.example.jwt_oauth.domain.board.FileInfo;
import com.example.jwt_oauth.domain.dto.BoardInfoDto;
import com.example.jwt_oauth.domain.dto.BoardListDto;
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
// @Transactional
public class BoardService {

    
    private final BoardRepository boardRepository;
    private final FileInfoRepository fileInfoRepository;

    String filePath = "D:\\fastcampus\\97_OAuth_Jwt_board\\jwt_oauth\\src\\main\\resources\\static\\files";

    /**
    * @date : 2023-01-20
    * @author : AJS
    * @Description: 파라미터 boardInfo → BoardApiResponse로 변경
    **/

    
    // @Transactional(propagation = Propagation.REQUIRED)
    public Header<BoardApiResponse> create(final CreateBoardRequest request, final List<MultipartFile> files
                                            , UserPrincipal userPrincipal){
        BoardInfo boardInfo = requestToBoard(request, userPrincipal);        

        BoardInfo boardSaved = setBoardInfo(userPrincipal, boardInfo, files);
        log.info("{}", read(boardSaved.getId()).getData());
        if(!files.isEmpty()){
            //boardapiresponse builder로 바꿀수있진않나??
            return Header.OK(new BoardApiResponse(boardSaved, boardSaved.getFileInfoList()));

        }else{
            return Header.OK(new BoardApiResponse(boardSaved));
        }

    }
   
    // @Transactional(readOnly = true)
    public Header<BoardApiResponse> read(final Long id){
        // BoardInfo boardInfo = boardRepository.findById(id).orElseThrow(() -> new NoSuchElementException("not found board"));
        BoardInfo boardInfo = boardRepository.readQuery(id).orElseThrow(() -> new NoSuchElementException("not found board"));
        log.info("test : {}", boardInfo);
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
                                            // .fileList(getOriginFileName(boardInfo.getFileInfoList()))
                                            .fileList(boardInfo.getFileInfoList().stream()
                                                                                .map(file -> setOriginFileName(file))
                                                                                .collect(Collectors.toList()))
                                            .build());
    }

    public Header<List<BoardApiResponse>> getBoardList(){
        
        // List<Boardlist> boardList = boardRepository.findByBoardStatus(BoardStatus.REGISTERED)
        //                                             .orElseThrow( () -> new RuntimeException("등록된 게시글이 없습니다."));
        List<Boardlist> boardList = boardRepository.getBoardList()
                                                    .orElseThrow(() -> new RuntimeException("등록된 게시글이 없습니다."));

        List<BoardApiResponse> boardApiResponses = new ArrayList<>();
        if(boardList.size()<1){
            return Header.OK(boardApiResponses);
        }        
        
        boardApiResponses = boardList.stream().map(board -> boardListDto(board))
                                                 .collect(Collectors.toList());

        return Header.OK(boardApiResponses);
    }
    
    // 권한 여부에 따른 수정 유무 로직 추가할것.
    @Transactional
    public Header<BoardApiResponse> update(final CreateBoardRequest request, final List<MultipartFile> files
                                                ,UserPrincipal userPrincipal, Long id){

        //// boardInfo 수정 메소드
        BoardInfo boardInfo = boardRepository.findById(id).get();

        boardInfo.setTitle(request.getTitle());
        boardInfo.setContent(request.getContent());
        // orphanremoval 때문에 deleteByBoardInfo 둘중 하나 없애기.
        boardInfo.getFileInfoList().clear();

        if(emptyFileChk(files)){
            
            fileInfoRepository.deleteByBoardInfo(boardInfo);
            
            List<FileInfo> fileList = files.stream()
                                            .map(file -> multiPartFileToFileInfo(file, boardInfo))
                                            .collect(Collectors.toList());
            
            fileInfoRepository.saveAll(fileList);
            
        }else{
            boardInfo.setFileInfoList(null);
            fileInfoRepository.saveAll(boardInfo.getFileInfoList());
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
                                            // .fileList(getOriginFileName(boardInfo.getFileInfoList()))
                                            .fileList(boardInfo.getFileInfoList())
                                            .build());
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
                                            // .fileList(getOriginFileName(boardInfo.getFileInfoList()))
                                            // .fileList(boardInfo.getFileInfoList().stream()
                                            //                                     .map(file -> setOriginFileName(file))
                                            //                                     .collect(Collectors.toList()))
                                            .fileList(boardInfo.getFileInfoList())
                                            .build());
    }

    
    // public Header<ApiResponse> delete(UserPrincipal userPrincipal, final Long id){
    @Transactional
    public void delete(UserPrincipal userPrincipal, final Long id){
        
        BoardInfo boardInfo = boardRepository.findById(id).get();
        
        if(userPrincipal.getAuthorities().equals("ROLE_ADMIN") || userPrincipal.getUserName().equals(boardInfo.getUserName())){
            
            // delete 로 게시글 비활성화
            boardInfo.setBoardStatus(BoardStatus.UNREGISTERED);

        }
        
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
        BoardApiResponse response;
        response = BoardApiResponse.builder()
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

    private BoardApiResponse boardListDto(Boardlist boardInfo){
        BoardApiResponse response;  
        
        response = BoardApiResponse.builder()
                                                    .id(boardInfo.getId())
                                                    .email(boardInfo.getEmail())
                                                    .userName(boardInfo.getUserName())
                                                    .title(boardInfo.getTitle())
                                                    .content(boardInfo.getContent())
                                                    .createdDate(boardInfo.getCreatedDate())
                                                    .createBy(boardInfo.getCreatedBy())
                                                    .modifiedDate(boardInfo.getModifiedDate())
                                                    .modifiedBy(boardInfo.getModifiedBy())
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

    private static FileInfo setOriginFileName(FileInfo file){
        // cf837476-a611-4274-a469-02569f7cb97d_GPU.PNG
        if(file.getFileName().equals("")){
            return null;
        }
                
        String[] splitName = file.getFileName().split("_");
        String originFileName = null;
        if(splitName.length == 1){
            originFileName = splitName[0];
        }else{
            originFileName = splitName[1];
        }
        
        file.setFileName(originFileName);
        // for (FileInfo fileInfo : fileList) {
        //     arr = fileInfo.getFileName().split("_");
        //     originFileName=arr[1];
        //     fileInfo.setFileName(originFileName);
        // }
        return file;
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

    private FileInfo multiPartFileToFileInfo(MultipartFile file){
        try{
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            File saveFile = new File(filePath,fileName);
            file.transferTo(saveFile);
            FileInfo fileInfo = new FileInfo.FileInfoBuilder()
                                            .fileName(fileName)
                                            .filePath("/files/" + fileName)
                                            .build();
            return fileInfo;
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
        
        return null;
    }
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

}
