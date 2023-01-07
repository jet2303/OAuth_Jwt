package com.example.jwt_oauth.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.ModelAndViewAssert.*;

import java.io.UnsupportedEncodingException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import com.example.jwt_oauth.payload.response.ApiResponse;
import com.example.jwt_oauth.payload.response.Message;
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

    @Before
    public void setup() throws Exception{
       this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                                    .addFilters(new CharacterEncodingFilter("UTF-8", true))
                                    .alwaysDo(MockMvcResultHandlers.print())
                                    .alwaysDo(MockMvcResultHandlers.log())
                                    .build();
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
        // signup();
        JSONObject jsonObject = signin();
        String accessToken = jsonObject.get("accessToken").toString();

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/list")
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

    @Test
    @Order(1)
    void testBoardCreate() throws Exception{
        signup();
        JSONObject jsonObject = signin();
        String accessToken = jsonObject.get("accessToken").toString();
        
        BoardInfoDto boardInfoDto = new BoardInfoDto.BoardInfoDtoBuilder()
                                                    .title("boardcreate title")
                                                    .content("boardcreate content")
                                                    .boardStatus(BoardStatus.REGISTERED.toString())
                                                    .fileName("boardcreate filename")
                                                    .filePath("boardcreate filepath")
                                                    .build();
        
        String content = objectMapper.writeValueAsString(boardInfoDto);

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/board/create")
                                                                            .header("Authorization", String.format("Bearer %s", accessToken))
                                                                            .content(content)                                                
                                                                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                                                    ).andDo(MockMvcResultHandlers.print());
        
        String result = resultActions.andReturn().getResponse().getContentAsString();
        String information = asStringToJson(result).get("information").toString();
        Message message = objectMapper.readValue(information, Message.class);
        
        assertEquals(message.getMessage(), "create success");
    }
    

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
