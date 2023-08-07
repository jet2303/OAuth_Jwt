package com.example.jwt_oauth.Renewal_230524.Service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.OverrideAutoConfiguration;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.autoconfigure.filter.TypeExcludeFilters;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTypeExcludeFilter;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.jwt_oauth.auth.WithAccount;
import com.example.jwt_oauth.config.security.token.UserPrincipal;
import com.example.jwt_oauth.domain.board.BoardInfo;
import com.example.jwt_oauth.domain.board.BoardStatus;
import com.example.jwt_oauth.domain.board.FileInfo;
import com.example.jwt_oauth.domain.board.FileInfo.FileInfoBuilder;
import com.example.jwt_oauth.domain.dto.BoardInfoDto;
import com.example.jwt_oauth.payload.Header;
import com.example.jwt_oauth.payload.error.ErrorCode;
import com.example.jwt_oauth.payload.error.RestApiException;
import com.example.jwt_oauth.payload.error.errorCodes.BoardErrorCode;
import com.example.jwt_oauth.payload.request.auth.SignInRequest;
import com.example.jwt_oauth.payload.request.board.CreateBoardRequest;
import com.example.jwt_oauth.payload.response.auth.AuthResponse;
import com.example.jwt_oauth.payload.response.board.BoardApiResponse;
import com.example.jwt_oauth.repository.board.BoardRepository;
import com.example.jwt_oauth.repository.board.FileInfoRepository;
import com.example.jwt_oauth.service.board.BoardService;
import com.example.jwt_oauth.service.user.auth.AuthService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
// @DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

// @OverrideAutoConfiguration(enabled = false)
@TypeExcludeFilters(DataJpaTypeExcludeFilter.class)
@Transactional
@AutoConfigureCache
@AutoConfigureDataJpa
@AutoConfigureTestDatabase
@AutoConfigureTestEntityManager
@ImportAutoConfiguration
public class BoardServiceTest extends MockBeans {

        private List<MultipartFile> mFiles = new ArrayList<>();

        @Test
        // @Disabled
        void 계정테스트() {
                SignInRequest request = SignInRequest.builder()

                                .email("test1@naver.com")
                                // .password("1234")
                                .password("admin")
                                .rememberMe("rememberMe")
                                .build();
                authService.provider_signin(request, mockHttpServletResponse);

                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                // UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

                assertNotNull(authentication);
        }

        // Create Success
        @Order(1)
        @Test
        void 게시글생성_성공() throws Exception {

                SignInRequest request = SignInRequest.builder()
                                .email("test1@naver.com")
                                .password("admin")
                                // .password("1234")
                                .rememberMe("rememberMe")
                                .build();
                log.info("Authentication : {}", SecurityContextHolder.getContext().getAuthentication());

                authService.signin(request, mockHttpServletResponse);

                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                //// 23.07.03 AJS 수정
                // UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

                UserPrincipal userPrincipal = UserPrincipal.builder()
                                .email((String) authentication.getPrincipal())
                                .password((String) authentication.getCredentials())
                                .userName(authentication.getName())
                                .authorities(authentication.getAuthorities())
                                .build();

                CreateBoardRequest boardRequest = CreateBoardRequest.builder()
                                .title("request title")
                                .email(userPrincipal.getEmail())
                                .userName(userPrincipal.getUserName())
                                .content("request content")
                                .boardStatus(BoardStatus.REGISTERED)
                                .build();

                MockMultipartFile file1 = new MockMultipartFile("image1",
                                "test.png",
                                "image/png",
                                new FileInputStream(
                                                "F:\\fastcampus\\97_OAuth_Jwt_board\\jwt_oauth\\src\\test\\java\\com\\example\\jwt_oauth\\resources\\test.png"));
                MockMultipartFile file2 = new MockMultipartFile("image2",
                                "test.png",
                                "image/png",
                                new FileInputStream(
                                                "F:\\fastcampus\\97_OAuth_Jwt_board\\jwt_oauth\\src\\test\\java\\com\\example\\jwt_oauth\\resources\\test.png"));

                mFiles.add((MultipartFile) file1);
                mFiles.add((MultipartFile) file2);

                Header<BoardApiResponse> result = boardService.create(boardRequest, mFiles, userPrincipal);

                Long id = result.getData().getId();

                assertEquals(boardService.read(id).getData().getTitle(), "request title");
                assertEquals(boardService.read(id).getData().getContent(), "request content");
                assertEquals(result.getData().getFileList().size(), 2);
        }

        // Create Error - AnonyMous
        @Test
        @Disabled
        void 게시글생성실패_Anonymous() throws Exception {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

                CreateBoardRequest boardRequest = CreateBoardRequest.builder()
                                .email(userPrincipal.getEmail())
                                .userName(userPrincipal.getUserName())
                                .title("request title")
                                .content("request content")
                                .boardStatus(BoardStatus.REGISTERED)
                                .build();

                MockMultipartFile file1 = new MockMultipartFile("image1",
                                "test.png",
                                "image/png",
                                new FileInputStream(
                                                "F:\\fastcampus\\97_OAuth_Jwt_board\\jwt_oauth\\src\\test\\java\\com\\example\\jwt_oauth\\resources\\test.png"));
                MockMultipartFile file2 = new MockMultipartFile("image2",
                                "test.png",
                                "image/png",
                                new FileInputStream(
                                                "F:\\fastcampus\\97_OAuth_Jwt_board\\jwt_oauth\\src\\test\\java\\com\\example\\jwt_oauth\\resources\\test.png"));

                mFiles.add((MultipartFile) file1);
                mFiles.add((MultipartFile) file2);

                assertThrows(RestApiException.class, () -> boardService.create(boardRequest, mFiles, userPrincipal));
        }

        // Create Error - empty title
        @Test
        @Order(2)
        void 게시글생성실패_EmptyTitle() throws Exception {
                SignInRequest request = SignInRequest.builder()
                                .email("test1@naver.com")
                                // .password("1234")
                                .password("admin")
                                .rememberMe("rememberMe")
                                .build();
                authService.signin(request, mockHttpServletResponse);

                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                //// 23.07.03 AJS 수정
                // UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

                UserPrincipal userPrincipal = UserPrincipal.builder()
                                .email((String) authentication.getPrincipal())
                                .password((String) authentication.getCredentials())
                                .userName(authentication.getName())
                                .authorities(authentication.getAuthorities())
                                .build();

                CreateBoardRequest boardRequest = CreateBoardRequest.builder()
                                .title(null)
                                .email(userPrincipal.getEmail())
                                .userName(userPrincipal.getUserName())
                                .content("request content")
                                .boardStatus(BoardStatus.REGISTERED)
                                .build();

                MockMultipartFile file1 = new MockMultipartFile("image1",
                                "test.png",
                                "image/png",
                                new FileInputStream(
                                                "F:\\fastcampus\\97_OAuth_Jwt_board\\jwt_oauth\\src\\test\\java\\com\\example\\jwt_oauth\\resources\\test.png"));
                MockMultipartFile file2 = new MockMultipartFile("image2",
                                "test.png",
                                "image/png",
                                new FileInputStream(
                                                "F:\\fastcampus\\97_OAuth_Jwt_board\\jwt_oauth\\src\\test\\java\\com\\example\\jwt_oauth\\resources\\test.png"));

                mFiles.add((MultipartFile) file1);
                mFiles.add((MultipartFile) file2);

                // 정확한 예외처리 할수있게 수정해야함..
                assertThrows(RestApiException.class, () -> boardService.create(boardRequest, mFiles, userPrincipal));
        }

        // Create Error - empty content
        @Test
        @Order(3)
        void 게시글생성_실패_EmptyContent() throws Exception {
                SignInRequest request = SignInRequest.builder()
                                .email("test1@naver.com")
                                // .password("1234")
                                .password("admin")
                                .rememberMe("rememberMe")
                                .build();
                authService.signin(request, mockHttpServletResponse);

                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                //// 23.07.03 AJS 수정
                // UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

                UserPrincipal userPrincipal = UserPrincipal.builder()
                                .email((String) authentication.getPrincipal())
                                .password((String) authentication.getCredentials())
                                .userName(authentication.getName())
                                .authorities(authentication.getAuthorities())
                                .build();

                CreateBoardRequest boardRequest = CreateBoardRequest.builder()
                                .title("request title")
                                .email(userPrincipal.getEmail())
                                .userName(userPrincipal.getUserName())
                                .content(null)
                                .boardStatus(BoardStatus.REGISTERED)
                                .build();

                MockMultipartFile file1 = new MockMultipartFile("image1",
                                "test.png",
                                "image/png",
                                new FileInputStream(
                                                "F:\\fastcampus\\97_OAuth_Jwt_board\\jwt_oauth\\src\\test\\java\\com\\example\\jwt_oauth\\resources\\test.png"));
                MockMultipartFile file2 = new MockMultipartFile("image2",
                                "test.png",
                                "image/png",
                                new FileInputStream(
                                                "F:\\fastcampus\\97_OAuth_Jwt_board\\jwt_oauth\\src\\test\\java\\com\\example\\jwt_oauth\\resources\\test.png"));

                mFiles.add((MultipartFile) file1);
                mFiles.add((MultipartFile) file2);

                // 정확한 예외처리 할수있게 수정해야함..
                assertThrows(RestApiException.class, () -> boardService.create(boardRequest, mFiles, userPrincipal));
        }

        // Read Success

        // Update Success

        @Test
        @WithAccount(value = "jsan")
        @Transactional
        @Order(4)
        void 업데이트_성공() {
                UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication()
                                .getPrincipal();

                CreateBoardRequest request = CreateBoardRequest.builder()
                                .title("title")
                                .email(userPrincipal.getEmail())
                                .userName(userPrincipal.getUserName())
                                .content("content")
                                .boardStatus(BoardStatus.REGISTERED)
                                .build();
                try {
                        MockMultipartFile file1 = new MockMultipartFile("image1",
                                        "test10.png",
                                        "image/png",
                                        new FileInputStream(
                                                        "F:\\fastcampus\\97_OAuth_Jwt_board\\jwt_oauth\\src\\test\\java\\com\\example\\jwt_oauth\\resources\\test.png"));
                        mFiles.add(file1);
                } catch (IOException e) {
                        e.printStackTrace();
                }

                Long createdId = boardService.create(request, mFiles, userPrincipal).getData().getId();

                // assertEquals(boardService.read(createdId).getData().getTitle(), "title");
                // assertEquals(boardService.read(createdId).getData().getContent(), "content");
                // assertEquals(boardService.read(createdId).getData().getFileList().size(), 1);

                CreateBoardRequest request1 = CreateBoardRequest.builder()
                                .title("update title")
                                .email(userPrincipal.getEmail())
                                .userName(userPrincipal.getUserName())
                                .content("update content")
                                .boardStatus(BoardStatus.REGISTERED)
                                .build();
                try {
                        MockMultipartFile file1 = new MockMultipartFile("image1",
                                        "test1.png",
                                        "image/png",
                                        new FileInputStream(
                                                        "F:\\fastcampus\\97_OAuth_Jwt_board\\jwt_oauth\\src\\test\\java\\com\\example\\jwt_oauth\\resources\\test3.png"));

                        MockMultipartFile file2 = new MockMultipartFile("image2",
                                        "test2.png",
                                        "image/png",
                                        new FileInputStream(
                                                        "F:\\fastcampus\\97_OAuth_Jwt_board\\jwt_oauth\\src\\test\\java\\com\\example\\jwt_oauth\\resources\\test3.png"));

                        mFiles.clear();
                        mFiles.add(file1);
                        mFiles.add(file2);

                } catch (IOException e) {
                        e.printStackTrace();
                }
                BoardInfoDto response = boardService.update1(request1, mFiles, userPrincipal, createdId).getData();

                BoardApiResponse response1 = boardService.read(response.getId()).getData();
                assertEquals(response1.getTitle(), "update title");
                assertEquals(response1.getContent(), "update content");
                assertEquals(response1.getFileList().size(), 2);
        }

        // Update Error - Session 만료, JWT 만료, 로그아웃 된상태

        // delete Success

        // delete Error - 글쓴계정과 삭제하려는 계정이 다를경우, 관리자 권한 확인

}
