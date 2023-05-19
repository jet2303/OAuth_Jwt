package com.example.jwt_oauth.service.board;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.jwt_oauth.config.security.token.CurrentUser;
import com.example.jwt_oauth.config.security.token.UserPrincipal;
import com.example.jwt_oauth.controller.BoardController;
import com.example.jwt_oauth.domain.board.BoardInfo;
import com.example.jwt_oauth.domain.board.BoardStatus;
import com.example.jwt_oauth.domain.board.FileInfo;
import com.example.jwt_oauth.domain.user.Role;
import com.example.jwt_oauth.payload.Header;
import com.example.jwt_oauth.payload.request.auth.SignInRequest;
import com.example.jwt_oauth.payload.request.auth.SignUpRequest;
import com.example.jwt_oauth.payload.request.board.CreateBoardRequest;
import com.example.jwt_oauth.payload.response.auth.AuthResponse;
import com.example.jwt_oauth.payload.response.board.BoardApiResponse;
import com.example.jwt_oauth.repository.board.BoardRepository;
import com.example.jwt_oauth.repository.board.FileInfoRepository;
import com.example.jwt_oauth.service.user.auth.AuthService;


import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@AutoConfigureMockMvc
// @ExtendWith(MockitoExtension.class)
@Slf4j
public class BoardServiceTest {

    @Autowired
    private BoardService boardService;

    @Autowired
    private BoardRepository boardRepository;

    

    private CreateBoardRequest request;
    private List<MultipartFile> mFiles = new ArrayList<>();
    private UserPrincipal userPrincipal;
    
    @BeforeEach
    @WithMockUser(username = "test@naver.com", password = "1234", roles = {"USER","ADMIN"})
    public void accountSetUp() throws Exception{

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetails userDetails = (UserDetails)principal;
        
        userPrincipal = new UserPrincipal(null, userDetails.getUsername(), userDetails.getPassword()
                                                            , userDetails.getAuthorities(), userDetails.getUsername());

        MockMultipartFile file1 = new MockMultipartFile("image1",
                                                "test.png",
                                                "image/png",
                                                new FileInputStream("D:\\fastcampus\\97_Oauth2_jwt\\jwt_oauth\\src\\test\\java\\com\\example\\jwt_oauth\\resources\\test.png"));
        MockMultipartFile file2 = new MockMultipartFile("image2",
                                                "test.png",
                                                "image/png",
                                                new FileInputStream("D:\\fastcampus\\97_Oauth2_jwt\\jwt_oauth\\src\\test\\java\\com\\example\\jwt_oauth\\resources\\test.png"));                                                
       
        mFiles.add((MultipartFile)file1);
        mFiles.add((MultipartFile)file2);
        
    
        
        request = CreateBoardRequest.builder()
                                    .title("title")
                                    .content("content")
                                    .userName(userPrincipal.getUserName())
                                    .boardStatus(BoardStatus.REGISTERED)
                                    .build();

        // boardService.create(request, mFiles, userPrincipal);
    }

    @AfterEach
    void accountTearDown(){
        // orphanRemoval = true 옵션으로 filerepository 제거 할필요 X.
        boardRepository.deleteAll();
    }

    @AfterAll
    static void after(){
        deleteFile();
    }

    private static void deleteFile(){
        String filePath = "D:\\fastcampus\\97_OAuth_Jwt_board\\jwt_oauth\\src\\main\\resources\\static\\files"; 
        File deleteFolder = new File(filePath);
        File[] deleteFileList = deleteFolder.listFiles();

        for (File file : deleteFileList) {
            file.delete();
        }  
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    @DisplayName(value = "create")
    @WithMockUser(username = "test@naver.com", password = "1234", roles = {"USER","ADMIN"})
    public void testCreate() throws Exception{
        // given                          

        // when
        Header<BoardApiResponse> response = boardService.create(request, mFiles, userPrincipal);
        
        // then
         
        Header<BoardApiResponse> response2 = boardService.read(response.getData().getId());
        
        // assertEquals(response.getData().getFileList().size() , response2.getData().getFileList().size());
    }

    //// 1.자식엔티티에 null로 세팅.
    @Test
    @DisplayName(value = "delete")
    @WithMockUser(username = "test11@naver.com", password = "1234", roles = {"USER"})
    void testDelete() throws Exception{
        
        Header<BoardApiResponse> resCreate = boardService.create(request, mFiles, userPrincipal);


        // Header<List<BoardApiResponse>> response = boardService.getBoardList();
        
        assertEquals(boardService.getBoardList().getData().size(), 1);
        
        boardService.delete(userPrincipal, resCreate.getData().getId());
        
        
        
        assertEquals(boardService.getBoardList().getData().size(), 0);
    }


    @Test
    @DisplayName(value = "GetList")
    @WithMockUser(username = "test@naver.com", password = "1234", roles = {"USER","ADMIN"})
    void testGetList() {
        boardService.create(request, mFiles, userPrincipal);

        assertEquals(boardService.getBoardList().getData().size(), 1);
    }

    @Test
    @DisplayName(value = "read")
    @WithMockUser(username = "test@naver.com", password = "1234", roles = {"USER"})
    
    void testRead(){

        Long id = boardService.create(request, mFiles, userPrincipal).getData().getId();

        Header<BoardApiResponse> response = boardService.read(id);
        String content = response.getData().getContent();
        String title = response.getData().getTitle();
        List<FileInfo> fList = response.getData().getFileList();
        log.info("{}", fList);
        assertEquals(title, "title");
        assertEquals(content, "content");
        assertEquals(fList.size(), 2);
    }

    @Test
    @DisplayName(value = "Update")
    @WithMockUser(username = "test@naver.com", password = "1234", roles = {"USER","ADMIN"})
    
    void testUpdate() throws IOException{
        Header<BoardApiResponse> beforeData = boardService.create(request, mFiles, userPrincipal);
        log.info("========================update====================== {}", beforeData.getData().getId());
        // assertEquals(boardService.read(beforeData.getData().getId()).getData().getTitle(), "title");
        // assertEquals(boardService.read(beforeData.getData().getId()).getData().getContent(), "content");
        // assertEquals(boardService.read(beforeData.getData().getId()).getData().getFileList().size(), 2);

        mFiles.clear();

        MockMultipartFile file1 = new MockMultipartFile("temp1",
                                                "temp1.txt",
                                                "text/html; charset=utf-8",
                                                new FileInputStream("D:\\fastcampus\\97_OAuth_Jwt_board\\jwt_oauth\\src\\test\\java\\com\\example\\jwt_oauth\\resources\\temp.txt"));
        MockMultipartFile file2 = new MockMultipartFile("temp2",
                                                "temp2.txt",
                                                "text/html; charset=utf-8",
                                                new FileInputStream("D:\\fastcampus\\97_OAuth_Jwt_board\\jwt_oauth\\src\\test\\java\\com\\example\\jwt_oauth\\resources\\temp.txt"));                                                
        MockMultipartFile file3 = new MockMultipartFile("temp3",
                                                "temp3.txt",
                                                "text/html; charset=utf-8",
                                                new FileInputStream("D:\\fastcampus\\97_OAuth_Jwt_board\\jwt_oauth\\src\\test\\java\\com\\example\\jwt_oauth\\resources\\temp.txt"));                                                                                                
        
        
        mFiles.add((MultipartFile)file1);
        mFiles.add((MultipartFile)file2);
        mFiles.add((MultipartFile)file3);
     
        request = CreateBoardRequest.builder()
                                    .title("update title")
                                    .content("update content")
                                    .build();

        Long resultId = boardService.update(request, mFiles, userPrincipal, beforeData.getData().getId())
                                    .getData().getId();
        
        BoardApiResponse resultData = boardService.read(resultId).getData();
        
        assertEquals(resultData.getTitle(), "update title");
        assertEquals(resultData.getContent(), "update content");
        assertEquals(resultData.getFileList().size(), 3);
        
        log.info("{}", resultData);
        // assertEquals(boardService.read(beforeData.getData().getId()).getData().getTitle(), "update title");
        // assertEquals(boardService.read(beforeData.getData().getId()).getData().getContent(), "update content");
        // assertEquals(boardService.read(beforeData.getData().getId()).getData().getFileList().size(), 3);
        
    }

    
}
