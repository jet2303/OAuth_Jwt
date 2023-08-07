package com.example.jwt_oauth.Renewal_230524.Service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import com.example.jwt_oauth.controller.GlobalExceptionHandler;
import com.example.jwt_oauth.domain.dto.UserDto;
import com.example.jwt_oauth.domain.user.Role;
import com.example.jwt_oauth.payload.error.CustomException;
import com.example.jwt_oauth.payload.request.auth.ChangePasswordRequest;
import com.example.jwt_oauth.payload.request.auth.SignUpRequest;
import com.example.jwt_oauth.payload.response.ApiResponse;
import com.example.jwt_oauth.payload.response.ExceptionResponse;
import com.example.jwt_oauth.payload.response.Message;
import com.example.jwt_oauth.service.user.UserService;
import com.example.jwt_oauth.service.user.auth.AuthService;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
public class UserServiceTest extends MockBeans {

    // 유저 생성 성공
    @Test
    public void 유저생성_성공() {
        SignUpRequest signUpRequest = SignUpRequest.builder()
                .name("test")
                .email("test@naver.com")
                .password("1234")
                .role(Role.USER.toString())
                .build();

        authService.signup(signUpRequest, mockHttpServletResponse);

        ResponseEntity<UserDto> response = userService.read("test@naver.com");

        assertEquals(response.getBody().getEmail(), "test@naver.com");
        assertEquals(response.getBody().getName(), "test");
        assertTrue(passwordEncoder.matches("1234", response.getBody().getPassword()));
        assertEquals(response.getBody().getRole().toString(), "USER");
        assertNotNull(response.getBody().getCreatedDate());
    }

    // 유저 생성 실패 - 중복 ID
    @Test
    public void 유저생성_실패_중복ID() {
        SignUpRequest signUpRequest = SignUpRequest.builder()
                .name("test")
                .email("test1@naver.com")
                .password("1234")
                .role(Role.USER.toString())
                .build();

        ResponseEntity<ApiResponse> response = authService.signup(signUpRequest, mockHttpServletResponse);
        log.info("{}", response.getBody().getNewInformation());
        // assertThrows(BadCredentialsException.class, () -> {
        // authService.signup(signUpRequest, mockHttpServletResponse);
        // });

    }

    // 유저 성공 - Read
    @Test
    public void 유저Read_성공() {
        UserDto userDto = userService.read("test1@naver.com").getBody();

        assertNotNull(userDto);
    }

    // 유저 실패 - read
    @Test
    public void 유저Read_실패() {

        // assertThrows(UsernameNotFoundException.class, () ->
        // userService.read("test111@naver.com").getBody()
        // );
        assertThrows(CustomException.class, () -> userService.read("test111@naver.com").getBody());
    }

    // 유저 성공 - list read
    // 유저 실패 - list read

    // 유저 수정 성공 - PW
    // @Test
    // @Disabled
    // public void 유저_Update_PW_성공(){
    // UserDto userDto = UserDto.builder()
    // .email("test1@naver.com")
    // .password("update1234")
    // .build();
    // ResponseEntity<ApiResponse> response = userService.update(userDto);

    // UserDto result = userService.read("test1@naver.com").getBody();

    // Message message = (Message) response.getBody().getNewInformation();
    // assertEquals(message.getMessage(), "수정 성공");
    // assertTrue(passwordEncoder.matches("update1234", result.getPassword()));
    // }

    @Test
    @Transactional
    public void 유저_Update_PW_성공() {

        ResponseEntity<UserDto> resUser1 = userService.read("test1@naver.com");
        assertTrue(passwordEncoder.matches("admin", resUser1.getBody().getPassword()));

        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .email("test1@naver.com")
                .oldPassword("admin")
                .newPassword("1234")
                .chkNewPassword("1234")
                .build();
        ResponseEntity<ApiResponse> responseEntity = userService.update_pw(request);
        ResponseEntity<UserDto> resUser = userService.read("test1@naver.com");

        // log.info("유저_Update_PW_성공1 : {}", passwordEncoder.matches("1234",
        // resUser.getBody().getPassword()));
        assertTrue(passwordEncoder.matches("admin", resUser.getBody().getPassword()));
    }

    // 유저 수정 성공 - 이름
    @Test
    // @Transactional
    public void 유저_update_이름_성공() {
        UserDto userDto = UserDto.builder()
                .email("test1@naver.com")
                .name("updateName")
                .password("admin")
                .build();
        ResponseEntity<ApiResponse> response = userService.update(userDto);

        UserDto result = userService.read("test1@naver.com").getBody();
        Message message = (Message) response.getBody().getNewInformation();

        assertEquals(message.getMessage(), "수정 성공");
        assertEquals(result.getName(), "updateName");
        // assertEquals(result.getName(), "testname");
    }

    // 유저 수정 성공 - 그외

    // 유저 수정 실패 - PW
    @Test
    @Transactional
    public void 유저_update_PW_실패_3자리_이하() {
        UserDto userDto = UserDto.builder()
                .email("test1@naver.com")
                .password("000")
                .build();
        ResponseEntity<ApiResponse> response = userService.update(userDto);

        UserDto result = userService.read("test1@naver.com").getBody();

        Message message = (Message) response.getBody().getNewInformation();

        assertEquals(message.getMessage(), "수정 실패");
        assertTrue(passwordEncoder.matches("admin", result.getPassword()));
    }

    // @Test
    // void test2() {
    // SignUpRequest signUpRequest = SignUpRequest.builder()
    // .name("test")
    // .email("test@naver.com")
    // .password("1234")
    // .role(Role.USER.toString())
    // .build();

    // authService.signup(signUpRequest, mockHttpServletResponse);
    // // String email = userService.read("test@naver.com").getBody().getEmail();
    // assertThrows(NoSuchElementException.class, () ->
    // userService.read("test2@naver.com"))
    // }

    // 유저 수정 실패 - 이름

    // 유저 수정 실패 - 그외

    // 유저 삭제 성공

    // 유저 삭제 실패

}
