package com.example.jwt_oauth.service.board;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
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
import com.example.jwt_oauth.domain.dto.FileInfoDto;
import com.example.jwt_oauth.domain.dto.BoardInfoDto.BoardInfoDtoBuilder;
import com.example.jwt_oauth.payload.Header;
import com.example.jwt_oauth.payload.Pagenation;
import com.example.jwt_oauth.payload.error.CustomException;
import com.example.jwt_oauth.payload.error.ErrorCode;
import com.example.jwt_oauth.payload.error.RestApiException;
import com.example.jwt_oauth.payload.error.errorCodes.BoardErrorCode;
import com.example.jwt_oauth.payload.error.errorCodes.UserErrorCode;
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
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;
    private final FileInfoRepository fileInfoRepository;
    private final EntityManager entityManager;

    // @Value("${my.file.file.labtop.path}")
    @Value("${my.file.file.pc.path}")
    private String filePath;

    private static String folderPath;

    // @Value("${my.file.folder.labtop.path}")
    @Value("${my.file.folder.pc.path}")
    public void setFolderPath(String folderPath) {
        BoardService.folderPath = folderPath;
    }

    /**
     * @date : 2023-01-20
     * @author : AJS
     * @Description: 파라미터 boardInfo → BoardApiResponse로 변경
     **/

    public Header<BoardApiResponse> create(final CreateBoardRequest request, final List<MultipartFile> files,
            UserPrincipal userPrincipal) {
        if (request.getTitle() == null) {
            throw new RestApiException(BoardErrorCode.CREATE_EMPTY_TITLE);
        }

        if (request.getContent() == null) {
            throw new RestApiException(BoardErrorCode.CREATE_EMPTY_CONTENT);
        }

        if (userPrincipal.getUserName() == null) {
            throw new RestApiException(UserErrorCode.ANONYMOUS_USER);
        }

        BoardInfo boardInfo = requestToBoard(request, userPrincipal);
        BoardInfoDto boardInfoDto = setBoardInfo(userPrincipal, boardInfo, files);

        if (!files.isEmpty()) {
            return Header.OK(BoardApiResponse.from(boardInfoDto));
        } else {
            return Header.OK(BoardApiResponse.from(boardInfoDto));
        }

    }

    public Header<BoardApiResponse> read(final Long id) {

        BoardInfo boardInfo = boardRepository.readQuery(id)
                .orElseThrow(() -> new RestApiException(BoardErrorCode.NOT_FOUND_BOARD));
        // return Header.OK(new BoardApiResponse(boardInfo));
        return Header.OK(BoardApiResponse.builder()
                .id(boardInfo.getId())
                .userName(boardInfo.getUserName())
                .title(boardInfo.getTitle())
                .content(boardInfo.getContent())
                .boardStatus(boardInfo.getBoardStatus().toString())
                .createdDate(boardInfo.getCreatedDate())
                .createBy(boardInfo.getCreatedBy())
                .modifiedDate(boardInfo.getModifiedDate())
                .modifiedBy(boardInfo.getModifiedBy())
                .fileList(boardInfo.getFileInfoList())
                .build());
    }

    public Header<List<BoardApiResponse>> getBoardList() {

        // List<Boardlist> boardList =
        // boardRepository.findByBoardStatus(BoardStatus.REGISTERED)
        // .orElseThrow( () -> new RuntimeException("등록된 게시글이 없습니다."));
        List<Boardlist> boardList = boardRepository.getBoardList()
                .orElseThrow(() -> new RestApiException(BoardErrorCode.NOT_FOUND_BOARDLIST));

        List<BoardApiResponse> boardApiResponses = new ArrayList<>();
        if (boardList.size() < 1) {
            return Header.OK(boardApiResponses);
        }

        boardApiResponses = boardList.stream().map(board -> boardListDto(board))
                .collect(Collectors.toList());

        return Header.OK(boardApiResponses);
    }

    // 권한 여부에 따른 수정 유무 로직 추가할것.
    @Transactional
    // @Modiying
    public Header<BoardApiResponse> update(final CreateBoardRequest request, final List<MultipartFile> files,
            UserPrincipal userPrincipal, Long id) {

        BoardInfo boardInfo = boardRepository.readQuery(id)
                .orElseThrow(() -> new RestApiException(BoardErrorCode.NOT_FOUND_BOARD));
        boardInfo.setTitle(request.getTitle())
                .setContent(request.getContent())
                .setEmail(request.getEmail())
                .setUserName(request.getUserName())
                .setBoardStatus(request.getBoardStatus());

        boardInfo.getFileInfoList().clear();
        List<FileInfo> fileList = new ArrayList<>();
        try {
            for (MultipartFile file : files) {
                String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                File saveFile = new File(folderPath, fileName);
                file.transferTo(saveFile);

                FileInfo fileInfo = FileInfo.builder()
                        .fileName(fileName)
                        .filePath("/files/" + fileName)
                        .boardInfo(boardInfo)
                        .build();
                fileList.add(fileInfo);
                boardInfo.addFile(fileInfo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        BoardInfo savedBoard = boardRepository.save(boardInfo);

        return Header.OK(BoardApiResponse.builder()
                .id(savedBoard.getId())
                .email(savedBoard.getEmail())
                .userName(savedBoard.getUserName())
                .title(savedBoard.getTitle())
                .content(savedBoard.getContent())
                .boardStatus(savedBoard.getBoardStatus().toString())
                .createdDate(savedBoard.getCreatedDate())
                .createBy(savedBoard.getCreatedBy())
                .modifiedDate(savedBoard.getModifiedDate())
                .modifiedBy(savedBoard.getModifiedBy())
                .fileList(savedBoard.getFileInfoList())
                .build());

    }

    @Transactional
    public void delete(UserPrincipal userPrincipal, final Long id) {

        BoardInfo boardInfo = boardRepository.findById(id).get();

        if (userPrincipal.getAuthorities().equals("ROLE_ADMIN")
                || userPrincipal.getUserName().equals(boardInfo.getUserName())) {

            // delete 로 게시글 비활성화
            boardInfo.setBoardStatus(BoardStatus.UNREGISTERED);

        }

    }

    public Header<List<BoardApiResponse>> search(Pageable pageable) {

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
    public Header<BoardApiResponse> findByFetchJoinId(Long id) {
        // Optional<BoardInfoDto> board = boardRepository.findByFetchJoinId(id);

        List<BoardInfo> board = entityManager
                .createQuery("SELECT b FROM BoardInfo b JOIN FETCH b.fileInfoList b.id=:id", BoardInfo.class)
                .setParameter("id", id)
                .getResultList();
        return Header.OK(BoardApiResponse.from(board.get(0)));
    }

    ///////////////////////////////// private
    ///////////////////////////////// method//////////////////////////////////////////////////////////////////////////

    private BoardApiResponse response(Boardlist boardList) {
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

    private BoardApiResponse boardListDto(Boardlist boardInfo) {
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

    private BoardInfo requestToBoard(CreateBoardRequest request, UserPrincipal userPrincipal) {
        BoardInfo boardInfo = BoardInfo.builder()
                .email(userPrincipal.getEmail())
                .userName(request.getUserName())
                .title(request.getTitle())
                .content(request.getContent())
                .boardStatus(request.getBoardStatus())
                .build();
        return boardInfo;
    }

    private boolean emptyFileChk(List<MultipartFile> files) {

        for (MultipartFile file : files) {

            if ("".equals(file.getOriginalFilename())) {
                return false;
            }
        }

        return true;
    }

    private static FileInfo setOriginFileName(FileInfo file) {
        // cf837476-a611-4274-a469-02569f7cb97d_GPU.PNG
        if (file.getFileName().equals("")) {
            return null;
        }

        String[] splitName = file.getFileName().split("_");
        String originFileName = null;
        if (splitName.length == 1) {
            originFileName = splitName[0];
        } else {
            originFileName = splitName[1];
        }

        file.setFileName(originFileName);

        return file;
    }

    private FileInfo multiPartFileToFileInfo(MultipartFile file, BoardInfo boardInfo) {
        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            File saveFile = new File(filePath, fileName);
            file.transferTo(saveFile);
            FileInfo fileInfo = FileInfo.builder()
                    .fileName(fileName)
                    .filePath("/files/" + fileName)
                    .boardInfo(boardInfo)
                    .build();
            return fileInfo;
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return null;
    }

    private FileInfo multiPartFileToFileInfo(MultipartFile file) {
        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            File saveFile = new File(filePath, fileName);
            file.transferTo(saveFile);
            // FileInfo fileInfo = new FileInfo.FileInfoBuilder()
            // .fileName(fileName)
            // .filePath("/files/" + fileName)
            // .build();
            FileInfo fileInfo = FileInfo.builder()
                    .fileName(fileName)
                    .filePath("/files/" + fileName)
                    .build();
            return fileInfo;
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return null;
    }

    private BoardInfoDto setBoardInfo(UserPrincipal userPrincipal, BoardInfo boardInfo,
            final List<MultipartFile> files) {

        BoardInfo newBoard = BoardInfo.builder()
                .email(userPrincipal.getEmail())
                .userName(userPrincipal.getUserName())
                .title(boardInfo.getTitle())
                .content(boardInfo.getContent())
                .boardStatus(BoardStatus.REGISTERED)
                .build();

        List<FileInfo> fileList = new ArrayList<>();
        try {
            if (files != null && emptyFileChk(files)) {
                for (MultipartFile file : files) {
                    String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                    // File saveFile = new File(filePath, fileName);
                    File saveFile = new File(folderPath, fileName);
                    file.transferTo(saveFile);

                    FileInfo fileInfo = FileInfo.builder()
                            .fileName(fileName)
                            .filePath("/files/" + fileName)
                            .boardInfo(newBoard)
                            .build();
                    fileList.add(fileInfo);
                    newBoard.addFile(fileInfo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        BoardInfo savedBoard = boardRepository.save(newBoard);

        List<FileInfoDto> fileInfoDtos = fileToFileDto(fileList);

        return new BoardInfoDto.BoardInfoDtoBuilder()
                .id(savedBoard.getId())
                .email(savedBoard.getEmail())
                .userName(savedBoard.getUserName())
                .title(savedBoard.getTitle())
                .content(savedBoard.getTitle())
                .boardStatus(savedBoard.getBoardStatus())
                .fileInfoList(fileInfoDtos)
                .createdBy(savedBoard.getCreatedBy())
                .createdDate(savedBoard.getCreatedDate())
                .build();
    }

    private List<FileInfoDto> fileToFileDto(List<FileInfo> fileList) {
        List<FileInfoDto> dtoList = new ArrayList<>();

        for (FileInfo fileInfo : fileList) {
            FileInfoDto dto = FileInfoDto.builder()
                    .id(fileInfo.getId())
                    .fileName(fileInfo.getFileName())
                    .filePath(fileInfo.getFilePath())
                    .build();
            dtoList.add(dto);
        }
        return dtoList;
    }
}
