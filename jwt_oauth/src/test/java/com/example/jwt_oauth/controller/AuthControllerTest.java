package com.example.jwt_oauth.controller;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.json.simple.JSONObject;
// import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.saml2.Saml2RelyingPartyProperties.Registration.Signing;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.example.jwt_oauth.config.security.token.UserPrincipal;
import com.example.jwt_oauth.domain.user.User;
import com.example.jwt_oauth.payload.request.auth.ChangePasswordRequest;
import com.example.jwt_oauth.payload.request.auth.RefreshTokenRequest;
import com.example.jwt_oauth.payload.request.auth.SignInRequest;
import com.example.jwt_oauth.payload.request.auth.SignUpRequest;
import com.example.jwt_oauth.repository.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.oauth2.sdk.util.JSONUtils;

import lombok.extern.slf4j.Slf4j;



@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
public class AuthControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext ctx;
    
    @Before
    public void setup(){
        mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                                    .addFilters(new CharacterEncodingFilter("UTF-8", true))
                                    .alwaysDo(MockMvcResultHandlers.print())
                                    .alwaysDo(MockMvcResultHandlers.log())
                                    .build();
    }

    @Test
    @Order(8)
    void testDelete() throws Exception{
        JSONObject jsonObject = signin();
        String authorization = jsonObject.get("accessToken").toString();

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.delete("/auth/")
                                                                            .header("Authorization", String.format("Bearer %s", authorization))
                                                                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                                                .andDo(MockMvcResultHandlers.print());
        JSONObject jObject = asStringToJson(resultActions.andReturn().getResponse().getContentAsString());
        JSONObject result = asStringToJson(jObject.get("information").toString());
        assertEquals("Delete 성공", result.get("message"));
    }

    @Test
    @DisplayName(value = "PW 정상 수정")
    @Order(5)
    void testModify1() throws Exception{
        JSONObject jsonObject = signin();
        String authorization = jsonObject.get("accessToken").toString();

        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setOldPassword("password");
        changePasswordRequest.setNewPassword("password1");
        changePasswordRequest.setChkNewPassword("password1");
        

        //UserPrincipal 은 Header의 Authorization 필드를 보고 값을 받아줌.
        
            ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/auth/")
                                                        .header("Authorization", String.format("Bearer %s", authorization))
                                                        .content(objectMapper.writeValueAsString(changePasswordRequest) )
                                                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                                                .andDo(MockMvcResultHandlers.print());
        String result = resultActions.andReturn().getResponse().getContentAsString();
        JSONObject jObject = asStringToJson(result);
        JSONObject information = asStringToJson(jObject.get("information").toString());
        
        
        assertEquals("수정되었습니다.", information.get("message"));
    
    }

    @Test
    @DisplayName(value = "PW 오타")
    @Order(6)
    void testModify2() throws Exception{
        JSONObject jsonObject = signin();
        String authorization = jsonObject.get("accessToken").toString();

        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setOldPassword("passwo");
        changePasswordRequest.setNewPassword("password1");
        changePasswordRequest.setChkNewPassword("password1");
        

        //UserPrincipal 은 Header의 Authorization 필드를 보고 값을 받아줌.
        
            ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/auth/")
                                                        .header("Authorization", String.format("Bearer %s", authorization))
                                                        .content(objectMapper.writeValueAsString(changePasswordRequest) )
                                                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                                                .andDo(MockMvcResultHandlers.print());
        String result = resultActions.andReturn().getResponse().getContentAsString();
        JSONObject jObject = asStringToJson(result);
        JSONObject information = asStringToJson(jObject.get("information").toString());
        
        
        assertEquals("기존 비밀번호가 일치하지 않습니다.", information.get("message"));
    
    }

    @Test
    @DisplayName(value = "PW 확인 오타")
    @Order(7)
    void testModify3() throws Exception{
        JSONObject jsonObject = signin();
        String authorization = jsonObject.get("accessToken").toString();

        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setOldPassword("password");
        changePasswordRequest.setNewPassword("password1");
        changePasswordRequest.setChkNewPassword("password2");
        

        //UserPrincipal 은 Header의 Authorization 필드를 보고 값을 받아줌.
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/auth/")
                                                    .header("Authorization", String.format("Bearer %s", authorization))
                                                    .content(objectMapper.writeValueAsString(changePasswordRequest) )
                                                    .contentType(MediaType.APPLICATION_JSON_VALUE))
                                            .andDo(MockMvcResultHandlers.print());
                                            
        String result = resultActions.andReturn().getResponse().getContentAsString();
        JSONObject jObject = asStringToJson(result);
        JSONObject information = asStringToJson(jObject.get("information").toString());

        
        assertEquals("새로운 비밀번호가 일치하지 않습니다.", information.get("message"));
    
    }

    @Test
    @Order(5)
    void testRefresh() throws Exception{
        JSONObject token = signin();
        String accessToken = token.get("refreshToken").toString();
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest(accessToken);

        log.info("access token : {}",accessToken);
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/auth/refresh")
                                                            .content(objectMapper.writeValueAsString(refreshTokenRequest))
                                                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                                                .andDo(MockMvcResultHandlers.print());
        String result = resultActions.andReturn().getResponse().getContentAsString();
        JSONObject jsonObject = asStringToJson(result);
        
        // assertEquals(accessToken, jsonObject.get("accessToken"));
        assertEquals(token.get("refreshToken"), jsonObject.get("refreshToken"));

    }

    @Test
    @Order(2)
    void testSignin() throws Exception{
        SignInRequest signInRequest = new SignInRequest();
        signInRequest.setEmail("test@naver.com");
        signInRequest.setPassword("password");

        String content = objectMapper.writeValueAsString(signInRequest);
        
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/signin")
                                                .content(content)
                                                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(MockMvcResultHandlers.print()); 

        
    }

    @Test
    // @Disabled
    @Order(4)
    void testSignout() throws Exception{
        JSONObject token = signin();
        String accessToken = token.get("refreshToken").toString();
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest(accessToken);
        
        SignInRequest signInRequest = new SignInRequest();
        signInRequest.setEmail("test@naver.com");
        signInRequest.setPassword("password");

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/signout")
                                                .content(objectMapper.writeValueAsString(signInRequest))
                                                .content(objectMapper.writeValueAsString(refreshTokenRequest))
                                                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Order(1)
    void testSignup() throws Exception{
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setEmail("test@naver.com");
        signUpRequest.setPassword("password");
        signUpRequest.setName("test");

        String content = objectMapper.writeValueAsString(signUpRequest);


        MvcResult mvcResult =
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/signup")
                                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                                .content(content) )
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
           
        log.info("{}", mvcResult.getResponse().getContentAsString());
        
    }

    @Test
    @Order(3)
    void testWhoAmI() throws Exception{
        JSONObject token = signin();
        String accessToken = token.get("accessToken").toString();

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/auth/")
                                                                                .header("Authorization", String.format("Bearer %s", accessToken))
                                                                                .contentType(MediaType.APPLICATION_JSON)
                                                                                .accept(MediaType.APPLICATION_JSON)
                                            ).andDo(MockMvcResultHandlers.print());

        JSONObject jsonObject = asStringToJson(resultActions.andReturn().getResponse().getContentAsString());
        log.info("jsonObject={}",jsonObject);

    }

    @Test
    // @Disabled
    void testLoginPage() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/auth/loginPage")
                                                .contentType(MediaType.APPLICATION_JSON_VALUE))
                                                .andDo(MockMvcResultHandlers.print());
    }


    private JSONObject signin() throws Exception{
        SignInRequest signInRequest = new SignInRequest();
        signInRequest.setEmail("test@naver.com");
        signInRequest.setPassword("password");

        ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.post("/auth/signin")
                                                                        .content(asJsonToString(signInRequest))
                                                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                                                        .accept(MediaType.APPLICATION_JSON_VALUE)
                                                                        )
                                        .andDo(MockMvcResultHandlers.print());
        log.info("result = {}", actions.andReturn().getResponse().getContentAsString());
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
