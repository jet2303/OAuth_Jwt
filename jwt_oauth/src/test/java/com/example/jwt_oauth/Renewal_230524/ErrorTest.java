package com.example.jwt_oauth.Renewal_230524;

import static org.junit.Assert.assertThrows;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.jwt_oauth.payload.error.CustomException;
import com.example.jwt_oauth.service.user.UserService;

@SpringBootTest
public class ErrorTest {

    @Autowired
    private UserService userService;

    @Test
    void USER_NOT_FOUND(){
        assertThrows(CustomException.class, () -> userService.read("test3@naver.com").getBody());
        
    }
}
