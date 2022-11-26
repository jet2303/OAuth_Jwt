package com.example.jwt_oauth.service.user;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.catalina.authenticator.SpnegoAuthenticator.AuthenticateAction;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.example.jwt_oauth.domain.dto.UserDto;
import com.example.jwt_oauth.domain.user.Provider;
import com.example.jwt_oauth.domain.user.Role;
import com.example.jwt_oauth.domain.user.User;
import com.example.jwt_oauth.domain.user.UserEnum;
import com.example.jwt_oauth.repository.user.UserRepository;
import com.example.jwt_oauth.service.user.auth.AuthService;

import lombok.extern.slf4j.Slf4j;

// @ActiveProfiles("test")
@SpringBootTest
@ExtendWith(SpringExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
public class UserServiceTest {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;


    @BeforeEach
    public void setup(){
        User newUser = User.builder()
                                    .name("test")
                                    .email("test@naver.com")
                                    .imageUrl("test imageUrl")
                                    .emailVerified(false)
                                    .password("password")
                                    .provider(Provider.kakao)
                                    .providerId("provierId")
                                    .role(Role.USER)
                                    .useyn(UserEnum.Y)
                                    .build();

        userRepository.save(newUser);
    }

    
    @Test
    @Transactional
    void testCreate() {
        
        UserDto newUser = UserDto.builder()
                                    .name("test")
                                    .email("test@naver.com")
                                    .imageUrl("test imageUrl")
                                    .emailVerified(false)
                                    .password("password")
                                    .provider(Provider.kakao)
                                    .providerId("provierId")
                                    .role(Role.USER)
                                    .useyn(UserEnum.Y)
                                    .build();

        log.info("{}", userService.create(newUser).getBody()); 
    }

    @Test
    @Transactional
    void testDelete() {
        User newUser = userRepository.findByName("test").get();
        log.info("{}", newUser);
        ResponseEntity<UserDto> responseEntity = userService.delete(newUser.getEmail());

        log.info("{}", responseEntity.getBody());
    }

    @Test
    @Order(1)
    void testRead() {

        
        ResponseEntity<UserDto> responseEntity = userService.read("test@naver.com");
        
        
        log.info("{}", responseEntity.getBody());
        
    }

    @Test
    @Transactional
    void testUpdate() {
        UserDto updateUser = UserDto.builder()
                                    .name("testUpdate")
                                    .email("test@naver.com")
                                    .useyn(UserEnum.Y)
                                    .build();

        ResponseEntity<UserDto> responseEntity = userService.update(updateUser);
        log.info("{}, {}, {}", responseEntity.getBody().getEmail(), responseEntity.getBody().getName(),responseEntity.getBody().getUseyn());
    }

    @Test
    @Disabled
    void testReadAll(){
        List<User> responseEntity = userRepository.findAll();
        
        for (User user : responseEntity) {
            log.info("{}", user.toString());
        }
    }

    @AfterEach
    public void deleteall(){
        userRepository.deleteAll();        
    }
}
