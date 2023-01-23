package com.example.jwt_oauth.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.ModelAndViewAssert.*;

import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.example.jwt_oauth.domain.board.BoardInfo;
import com.example.jwt_oauth.domain.board.BoardStatus;
import com.example.jwt_oauth.domain.dto.BoardInfoDto;
import com.example.jwt_oauth.payload.request.board.CreateBoardRequest;
import com.example.jwt_oauth.payload.response.ApiResponse;
import com.example.jwt_oauth.payload.response.Message;
import com.example.jwt_oauth.payload.response.board.BoardApiResponse;
import com.example.jwt_oauth.repository.board.BoardRepository;
import com.example.jwt_oauth.service.board.BoardService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
public class PageControllerTest {

    @Autowired
    private WebApplicationContext ctx;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private BoardService boardService;

    private String accessToken;

    @Before
    public void setup() throws Exception{
       this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                                    .addFilters(new CharacterEncodingFilter("UTF-8", true))
                                    .build();
    }

    @BeforeEach
    public void getAccessToken() throws Exception{

        // String email = "test@naver.com";
        // String password = "password";

        // ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.post("/auth/signin")
        //                                                                 .param("email",email)
        //                                                                 .param("password",password)
        //                                                                 .contentType(MediaType.APPLICATION_JSON_VALUE)
        //                                                                 );
                                        
        // // log.info("result = {}", actions.andReturn().getResponse().getContentAsString());
        // JSONObject jsonObject = asStringToJson(actions.andReturn().getResponse().getContentAsString());
        // this.accessToken =(String) jsonObject.get("accessToken");
    }

    @After
    public void after() throws Exception{
        JSONObject jsonObject = signin();
        String accessToken = jsonObject.get("accessToken").toString();
        mockMvc.perform(MockMvcRequestBuilders.get("/auth/signout")
                                                .header("Authorization", String.format("Bearer %s", accessToken))
                                                .contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    @Order(3)
    void testBoardlistService() throws Exception{
        JSONObject jsonObject = signin();
        String accessToken = jsonObject.get("accessToken").toString();

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/list")
                                                                    .header("Authorization", String.format("Bearer %s", accessToken))
                                                                    .contentType(MediaType.APPLICATION_JSON_VALUE))
                                            .andDo(MockMvcResultHandlers.print())
                                            .andExpect(MockMvcResultMatchers.model().attributeExists("boardList"))
                                            .andReturn();
        // boardService.getList(null)                                            
    }

    @Test
    @Order(2)
    void testBoardlist() throws Exception{
        signup();
        JSONObject jsonObject = signin();
        String accessToken = jsonObject.get("accessToken").toString();

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/page")
                                                                    .header("Authorization", String.format("Bearer %s", accessToken))
                                                                    .contentType(MediaType.APPLICATION_JSON_VALUE))
                                            .andDo(MockMvcResultHandlers.print())
                                            .andExpect(MockMvcResultMatchers.model().attributeExists("boardList"))
                                            .andReturn();

        log.info("{}", mvcResult.getModelAndView().getModel().get("boardList"));
        assertEquals(mvcResult.getModelAndView().getViewName(), "/page/list");
        
        // JSONParser jsonParser = new JSONParser();
        // JSONObject jObject = (JSONObject) jsonParser.parse(mvcResult.getModelAndView().getModel().get("boardList").toString());
        // log.info("{}", jObject);
        
    }

    // private JSONParser getValue(Object model){
    //     String[] value = model.toString().split(",");
    //     JSONObject jsonObject = new JSONObject();
        
    // }

    // @Test
    // @Order(1)
    // @Transactional(rollbackOn = {RuntimeException.class})
    // void testBoardCreate() throws Exception{
    //     signup();
    //     JSONObject jsonObject = signin();
    //     String accessToken = jsonObject.get("accessToken").toString();
        
    //     CreateBoardRequest request = new CreateBoardRequest("title", "content" , BoardStatus.REGISTERED);
        
    //     MockMultipartFile file1 = new MockMultipartFile("uploadfiles",
    //                                             "test.png",
    //                                             "image/png",
    //                                             new FileInputStream("D:\\fastcampus\\97_Oauth2_jwt\\jwt_oauth\\src\\test\\java\\com\\example\\jwt_oauth\\resources\\test.png"));
    //     MockMultipartFile file2 = new MockMultipartFile("uploadfiles",
    //                                             "test.png",
    //                                             "image/png",
    //                                             new FileInputStream("D:\\fastcampus\\97_Oauth2_jwt\\jwt_oauth\\src\\test\\java\\com\\example\\jwt_oauth\\resources\\test.png"));                                                
        
    //     List<MockMultipartFile> files = new ArrayList<>();
    //     files.add(file1);
    //     files.add(file2);


    //     String content = objectMapper.writeValueAsString(request);

    //     ResultActions result = mockMvc.perform(MockMvcRequestBuilders.multipart("/board/create")
    //                                                                     .file(file1)
    //                                                                     .file(file2)
    //                                                                     .param("title", request.getTitle())
    //                                                                     .param("content", request.getContent())
    //                                                                     .param("boardStatus", request.getBoardStatus().toString())
    //                                                                     .header("Authorization", String.format("Bearer %s", accessToken))
    //                                                                     .contentType(MediaType.APPLICATION_JSON_VALUE)
    //                                             ).andDo(MockMvcResultHandlers.print());
        
    //     // log.info("{}", result.andReturn().getResponse().getContentAsString());    
    //     assertEquals(boardRepository.findAll().size(), 1);
    //     assertEquals(boardRepository.findById(1L).get().getFileInfoList().size(), 2);
    //     assertEquals(result.andReturn().getResponse().getRedirectedUrl(), "/page");
        
    // }
    

    private void signup() throws Exception{
        String email = "test@naver.com";
        String password = "password";
        String name = "test";
        String role = "ADMIN";
        
        
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/signup")
                                                .param("email", email)
                                                .param("password", password)
                                                .param("name", name)
                                                .param("role", role)
                                                .contentType(MediaType.APPLICATION_JSON_VALUE) );
    }
    
    private JSONObject signin() throws Exception{
        String email = "test@naver.com";
        String password = "password";

        ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.post("/auth/signin")
                                                                        .param("email",email)
                                                                        .param("password",password)
                                                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                                                        );
                                        
        // log.info("result = {}", actions.andReturn().getResponse().getContentAsString());
        JSONObject jsonObject = asStringToJson(actions.andReturn().getResponse().getContentAsString());
        return jsonObject;
    }

    public static String asJsonToString(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //string 값을 json 형식으로 변경
    public static JSONObject asStringToJson(String string) throws ParseException{
        JSONParser jsonParser = new JSONParser();
        Object object = jsonParser.parse( string );
        JSONObject jsonObject = (JSONObject) object;
        return jsonObject;
    }
}
