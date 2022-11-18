package com.example.jwt_oauth.controller;

import java.util.List;

import javax.annotation.PostConstruct;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.example.jwt_oauth.config.security.token.UserPrincipal;
import com.example.jwt_oauth.domain.user.User;
import com.example.jwt_oauth.payload.request.auth.SignUpRequest;
import com.example.jwt_oauth.repository.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;


@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class AuthControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testDelete() {

    }

    @Test
    void testModify() {

    }

    @Test
    void testRefresh() {

    }

    @Test
    void testSignin() {

    }

    @Test
    void testSignout() {
        
    }

    @Test
    void testSignup() throws Exception{
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setEmail("test@naver.com");
        signUpRequest.setPassword("password");

        String content = objectMapper.writeValueAsString(signUpRequest);


        MvcResult mvcResult =
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/signup1")
                                                .content(content) )
                                                .andDo(MockMvcResultHandlers.print())
                                                .andReturn();
           
        log.info("{}", mvcResult.getResponse().getContentAsString());
        // List<User> userList = userRepository.findAll();    
        // for (User user : userList) {
        //     log.info("{}", user.getEmail());
        // }
    }

    @Test
    void testWhoAmI() {
        
    }
}
