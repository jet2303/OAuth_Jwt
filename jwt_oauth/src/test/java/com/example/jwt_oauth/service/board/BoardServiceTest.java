package com.example.jwt_oauth.service.board;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import com.example.jwt_oauth.domain.board.BoardInfo;

@SpringBootTest
@AutoConfigureMockMvc
public class BoardServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BoardService boardService;

    @Test
    void testCreate() throws Exception{
        // given
        BoardInfo boardInfo = new BoardInfo.BoardInfoBuilder()
                                            .id(1L)
                                            .title("title")
                                            .content("content")
                                            .boardStatus(null)
                                            .build();
                                                
                                         
        // MockMultipartFile file1 = new MockMultipartFile("image1",
        //                                         "test.png",
        //                                         "image/png",
        //                                         new FileInputStream("D:\\fastcampus\\97_Oauth2_jwt\\jwt_oauth\\src\\test\\java\\com\\example\\jwt_oauth\\resources\\test.png"));
        // MockMultipartFile file2 = new MockMultipartFile("image2",
        //                                         "test.png",
        //                                         "image/png",
        //                                         new FileInputStream("D:\\fastcampus\\97_Oauth2_jwt\\jwt_oauth\\src\\test\\java\\com\\example\\jwt_oauth\\resources\\test.png"));                                                
        
        // List<MockMultipartFile> files = new ArrayList<>();
        // files.add(file1);
        // files.add(file2);
        
        // when

        // boardService.create(boardInfo, files);

        // then
    }

    @Test
    void testDelete() {

    }

    @Test
    void testGetList() {

    }

    @Test
    void testRead() {

    }

    @Test
    void testUpdate() {

    }
}
