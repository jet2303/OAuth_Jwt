package com.example.jwt_oauth.Renewal_230524.Service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.autoconfigure.filter.TypeExcludeFilters;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTypeExcludeFilter;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.jwt_oauth.auth.WithAccount;
import com.example.jwt_oauth.config.security.token.UserPrincipal;
import com.example.jwt_oauth.domain.board.BoardStatus;
import com.example.jwt_oauth.payload.Header;
import com.example.jwt_oauth.payload.error.RestApiException;
import com.example.jwt_oauth.payload.request.auth.SignInRequest;
import com.example.jwt_oauth.payload.request.board.CreateBoardRequest;
import com.example.jwt_oauth.payload.response.board.BoardApiResponse;
import com.example.jwt_oauth.service.board.BoardService;

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

        @Value("${my.file.file.pc.path}")
        private String filePath;

        private static String folderPath;

        @Autowired
        private BoardService boardService;

        @Value("${my.file.folder.pc.path}")
        public void setFolderPath(String folderPath) {
                BoardServiceTest.folderPath = folderPath;
        }

        @BeforeEach
        void setUp() {
                SignInRequest request = SignInRequest.builder()
                                .email("test1@naver.com")
                                .password("admin")
                                .rememberMe("rememberMe")
                                .build();
                authService.provider_signin(request, mockHttpServletResponse);
                log.info("=======================before each=======================");
        }

        @AfterEach
        void setDown() {
                userService.delete("test1@naver.com");
        }

        @AfterAll
        static void last() {
                deleteFile();
        }

        private static void deleteFile() {
                String filePath = folderPath;
                // String filePath =
                // "C:\\Users\\Su\\Desktop\\Spring\\OAuth_Jwt\\jwt_oauth\\src\\main\\resources\\static\\files";
                File deleteFolder = new File(filePath);
                File[] deleteFileList = deleteFolder.listFiles();
                for (File file : deleteFileList) {
                        file.delete();
                }
        }

        @Test
        // @Disabled
        void 계정테스트() {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                // UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

                assertNotNull(authentication);
        }

        // Create Success
        @Order(1)
        @Test
        void 게시글생성_성공() throws Exception {
                log.info("{} {}", filePath, folderPath);
                Header<BoardApiResponse> result = boardCreate(1, 2);

                Long id = result.getData().getId();
                assertEquals("request title 1", boardService.read(id).getData().getTitle());
                assertEquals("request content 1", boardService.read(id).getData().getContent());
                assertEquals(2, result.getData().getFileList().size());
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
                                new FileInputStream(filePath));
                MockMultipartFile file2 = new MockMultipartFile("image2",
                                "test.png",
                                "image/png",
                                new FileInputStream(filePath));

                mFiles.add((MultipartFile) file1);
                mFiles.add((MultipartFile) file2);

                assertThrows(RestApiException.class, () -> boardService.create(boardRequest, mFiles, userPrincipal));
        }

        // Create Error - empty title
        @Test
        @Order(2)
        void 게시글생성실패_EmptyTitle() throws Exception {
                // SignInRequest request = SignInRequest.builder()
                // .email("test1@naver.com")
                // // .password("1234")
                // .password("admin")
                // .rememberMe("rememberMe")
                // .build();
                // authService.signin(request, mockHttpServletResponse);

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
                                new FileInputStream(filePath));

                MockMultipartFile file2 = new MockMultipartFile("image2",
                                "test.png",
                                "image/png",
                                new FileInputStream(filePath));

                mFiles.add((MultipartFile) file1);
                mFiles.add((MultipartFile) file2);

                // 정확한 예외처리 할수있게 수정해야함..
                assertThrows(RestApiException.class, () -> boardService.create(boardRequest, mFiles, userPrincipal));
        }

        // Create Error - empty content
        @Test
        @Order(3)
        void 게시글생성_실패_EmptyContent() throws Exception {
                // SignInRequest request = SignInRequest.builder()
                // .email("test1@naver.com")
                // // .password("1234")
                // .password("admin")
                // .rememberMe("rememberMe")
                // .build();
                // authService.signin(request, mockHttpServletResponse);

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
                                new FileInputStream(filePath));

                MockMultipartFile file2 = new MockMultipartFile("image2",
                                "test.png",
                                "image/png",
                                new FileInputStream(filePath));

                mFiles.add((MultipartFile) file1);
                mFiles.add((MultipartFile) file2);

                // 정확한 예외처리 할수있게 수정해야함..
                assertThrows(RestApiException.class, () -> boardService.create(boardRequest, mFiles, userPrincipal));
        }

        // Read Success

        // Update Success

        @Test
        // @WithAccount(value = "jsan")
        @Transactional
        @Order(4)
        void 업데이트_성공() {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                UserPrincipal userPrincipal = UserPrincipal.builder()
                                .email((String) authentication.getPrincipal())
                                .password((String) authentication.getCredentials())
                                .userName(authentication.getName())
                                .authorities(authentication.getAuthorities())
                                .build();

                // CreateBoardRequest request = CreateBoardRequest.builder()
                // .title("title")
                // .email(userPrincipal.getEmail())
                // .userName(userPrincipal.getUserName())
                // .content("content")
                // .boardStatus(BoardStatus.REGISTERED)
                // .build();
                // try {
                // MockMultipartFile file1 = new MockMultipartFile("image1",
                // "test10.png",
                // "image/png",
                // new FileInputStream(filePath));

                // mFiles.add(file1);
                // } catch (IOException e) {
                // e.printStackTrace();
                // }

                // Long createdId = boardService.create(request, mFiles,
                // userPrincipal).getData().getId();
                Long createdId = boardCreate(1, 2).getData().getId();

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
                                        new FileInputStream(filePath));

                        MockMultipartFile file2 = new MockMultipartFile("image2",
                                        "test2.png",
                                        "image/png",
                                        new FileInputStream(filePath));
                        MockMultipartFile file3 = new MockMultipartFile("image3",
                                        "test3.png",
                                        "image/png",
                                        new FileInputStream(filePath));
                        mFiles.clear();
                        mFiles.add(file1);
                        mFiles.add(file2);
                        mFiles.add(file3);
                } catch (IOException e) {
                        e.printStackTrace();
                }

                BoardApiResponse response = boardService.update(request1, mFiles, userPrincipal, createdId).getData();
                BoardApiResponse response1 = boardService.read(response.getId()).getData();
                assertEquals("update title", response1.getTitle());
                assertEquals("update content", response1.getContent());
                assertEquals(3, response1.getFileList().size());
        }

        // Update Error - Session 만료, JWT 만료, 로그아웃 된상태

        // delete Success

        // delete Error - 글쓴계정과 삭제하려는 계정이 다를경우, 관리자 권한 확인
        private Header<BoardApiResponse> boardCreate(final int boardCnt, final int fileCnt) {
                CreateBoardRequest boardRequest = null;

                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                UserPrincipal userPrincipal = UserPrincipal.builder()
                                .email((String) authentication.getPrincipal())
                                .password((String) authentication.getCredentials())
                                .userName(authentication.getName())
                                .authorities(authentication.getAuthorities())
                                .build();

                for (int i = 1; i <= boardCnt; i++) {
                        boardRequest = CreateBoardRequest.builder()
                                        .title("request title " + String.valueOf(i))
                                        .email(userPrincipal.getEmail())
                                        .userName(userPrincipal.getUserName())
                                        .content("request content " + String.valueOf(i))
                                        .boardStatus(BoardStatus.REGISTERED)
                                        .build();
                }

                for (int i = 1; i <= fileCnt; i++) {
                        try {
                                MockMultipartFile file = new MockMultipartFile("image " + String.valueOf(i),
                                                "test.png",
                                                "image/png",
                                                new FileInputStream(filePath));

                                mFiles.add((MultipartFile) file);

                        } catch (IOException e) {
                                e.printStackTrace();
                        }
                }

                Header<BoardApiResponse> result = boardService.create(boardRequest, mFiles, userPrincipal);
                return result;
        }
}
