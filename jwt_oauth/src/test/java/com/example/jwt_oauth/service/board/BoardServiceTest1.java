package com.example.jwt_oauth.service.board;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.multipart.MultipartFile;

import com.example.jwt_oauth.auth.WithAccount;
import com.example.jwt_oauth.config.security.token.UserPrincipal;
import com.example.jwt_oauth.domain.board.BoardStatus;
import com.example.jwt_oauth.payload.request.board.CreateBoardRequest;
import com.example.jwt_oauth.repository.board.BoardRepository;
import com.example.jwt_oauth.repository.user.UserRepository;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
public class BoardServiceTest1 {
    
    @Autowired
    private BoardService boardService;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private UserRepository userRepository;

    private CreateBoardRequest request;
    private List<MultipartFile> mFiles = new ArrayList<>();
    private UserPrincipal userPrincipal;

    @Before
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

    @Test
    @WithAccount(value = "jsan")
    public void test(){        
        log.info("{}", userRepository.findAll());
        
    }
}
