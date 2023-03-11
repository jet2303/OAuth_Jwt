package com.example.jwt_oauth.service.board;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.io.FileInputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import com.example.jwt_oauth.config.security.token.CurrentUser;
import com.example.jwt_oauth.config.security.token.UserPrincipal;
import com.example.jwt_oauth.controller.BoardController;
import com.example.jwt_oauth.domain.board.BoardInfo;
import com.example.jwt_oauth.payload.Header;
import com.example.jwt_oauth.payload.request.board.CreateBoardRequest;
import com.example.jwt_oauth.payload.response.board.BoardApiResponse;
import com.example.jwt_oauth.repository.board.BoardRepository;
import com.example.jwt_oauth.repository.board.FileInfoRepository;
import com.nimbusds.oauth2.sdk.Request;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@AutoConfigureMockMvc
// @ExtendWith(MockitoExtension.class)
@Slf4j
public class BoardServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BoardService boardService;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private FileInfoRepository fileInfoRepository;

    @BeforeTransaction
    public void accountSetUp(){

    }

    @AfterTransaction
    public void deleteSetUp(){

    }

    @Test
    @Order(1)
    @WithMockUser(username = "test@naver.com", password = "1234", roles = {"USER","ADMIN"})
    void testCreate() throws Exception{
        // given

        // BoardInfo boardInfo = new BoardInfo.BoardInfoBuilder()
        //                                     .id(1L)
        //                                     .title("title")
        //                                     .content("content")
        //                                     .boardStatus(null)
        //                                     .build();
                                                
                                         
        MockMultipartFile file1 = new MockMultipartFile("image1",
                                                "test.png",
                                                "image/png",
                                                new FileInputStream("D:\\fastcampus\\97_Oauth2_jwt\\jwt_oauth\\src\\test\\java\\com\\example\\jwt_oauth\\resources\\test.png"));
        MockMultipartFile file2 = new MockMultipartFile("image2",
                                                "test.png",
                                                "image/png",
                                                new FileInputStream("D:\\fastcampus\\97_Oauth2_jwt\\jwt_oauth\\src\\test\\java\\com\\example\\jwt_oauth\\resources\\test.png"));                                                
        
        List<MultipartFile> mFiles = new ArrayList<>();
        mFiles.add((MultipartFile)file1);
        mFiles.add((MultipartFile)file2);
        
        List<MockMultipartFile> files = new ArrayList<>();
        files.add(file1);
        files.add(file2);
        
        CreateBoardRequest request = CreateBoardRequest.builder()
                                                        .title("title")
                                                        .content("content")
                                                        .build();

        // MultipartFile mockMultipartFile = new MockMultipartFile("image1", "test.png", "image/png", "D:\\fastcampus\\97_Oauth2_jwt\\jwt_oauth\\src\\test\\java\\com\\example\\jwt_oauth\\resources\\test.png");

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetails userDetails = (UserDetails)principal;
        
        UserPrincipal userPrincipal = new UserPrincipal(null, userDetails.getUsername(), userDetails.getPassword()
                                                            , userDetails.getAuthorities(), userDetails.getUsername());
        // when

    
        Header<BoardApiResponse> response = boardService.create(request, mFiles, userPrincipal);
        
        
        // then
        // log.info("{}", boardRepository.getReferenceById(1));
        
        Header<BoardApiResponse> response2 = boardService.read(1L);
        log.info("{}", fileInfoRepository.findAll());
        assertEquals(response.getData().getFileList().size() , response2.getData().getFileList().size());
    }

    @Test
    void testDelete() {

    }

    @Test
    void testGetList() {

    }

    @Test
    void testRead() {
        // Header<BoardApiResponse> response = boardService.read(1L);
        // assertEquals(response.getData().getFileList(), 2);
    }

    @Test
    void testUpdate() {

    }
}
