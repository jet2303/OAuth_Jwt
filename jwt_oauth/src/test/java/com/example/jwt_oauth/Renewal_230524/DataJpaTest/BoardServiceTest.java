package com.example.jwt_oauth.Renewal_230524.DataJpaTest;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.InvocationInterceptor.Invocation;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import com.example.jwt_oauth.auth.WithAccount;
import com.example.jwt_oauth.auth.WithAccountSecurityContextFactory;
import com.example.jwt_oauth.config.security.token.UserPrincipal;
import com.example.jwt_oauth.domain.board.BoardInfo;
import com.example.jwt_oauth.domain.board.BoardStatus;
import com.example.jwt_oauth.domain.dto.FileInfoDto;
import com.example.jwt_oauth.payload.Header;
import com.example.jwt_oauth.payload.request.board.CreateBoardRequest;
import com.example.jwt_oauth.payload.response.board.BoardApiResponse;
import com.example.jwt_oauth.repository.board.BoardRepository;
import com.example.jwt_oauth.service.board.BoardService;
import com.example.jwt_oauth.service.user.UserService;
import com.example.jwt_oauth.service.user.auth.CustomUserDetailsService;
import com.nimbusds.jose.proc.SecurityContext;

import lombok.extern.slf4j.Slf4j;

@DataJpaTest
@Transactional(readOnly = true)
@Slf4j
public class BoardServiceTest {

    @Spy
    private BoardRepository boardRepository;

    @Mock
    private EntityManager entityManager;

    @Spy
    @InjectMocks
    private BoardService boardService;

    @Test
    void create() {
        // given
        BoardApiResponse response = BoardApiResponse.builder()
                .email("test@naver.com")
                .userName("userName")
                .title("bdd title")
                .content("bdd content")
                .boardStatus(BoardStatus.REGISTERED.toString())
                .createdDate(LocalDateTime.now())
                .createBy("ADMIN")
                .fileList(null)
                .build();

        BoardInfo boardInfo = new BoardInfo(null, "test@naver.com", "testUser", "testtitle", "test content", BoardStatus.REGISTERED, null);

        CreateBoardRequest request = new CreateBoardRequest(5L, "title", "email", "userName", "content",
                BoardStatus.REGISTERED);
        UserPrincipal userPrincipal = new UserPrincipal(3L, "test2@naver.com",
                "1234", Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")),
                "test2");

        // when
        when(boardRepository.save(any()))
                        .thenReturn(boardInfo);
        // when(boardService.create(request, null, userPrincipal)).thenReturn(Header.OK(response));
        doReturn(Header.OK(response)).when(boardService).create(request, null, userPrincipal);
        assertEquals("bdd content", boardService.create(request, null, userPrincipal).getData().getContent());

    }

    @Test
    void read() {
        // given
        BoardApiResponse response = BoardApiResponse.builder()
                .email("test@naver.com")
                .userName("userName")
                .title("bdd title")
                .content("bdd content")
                .boardStatus(BoardStatus.REGISTERED.toString())
                .createdDate(LocalDateTime.now())
                .createBy("ADMIN")
                .fileList(null)
                .build();
        // when
        when(boardService.read(any())).thenReturn(Header.OK(response));

        // then
        BoardApiResponse result = boardService.read(1L).getData();
        assertEquals(response.getEmail(), result.getEmail());
        assertEquals(response.getUserName(), result.getUserName());
        assertEquals(response.getContent(), result.getContent());
        assertEquals(response.getTitle(), result.getTitle());
        assertNull(result.getFileList());
    }

    @Test
    void update() {
        CreateBoardRequest request = new CreateBoardRequest(3L, "title", "email", "userName", "content",
        BoardStatus.REGISTERED);
        UserPrincipal userPrincipal = new UserPrincipal(3L, "test2@naver.com",
        "1234", Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")),
        "test2");

        boardRepository.save(BoardInfo.builder()
                                        .email("test@naver.com")
                                        .userName("update test name")
                                        .title("before update title")
                                        .content("before update content")
                                        .boardStatus(BoardStatus.REGISTERED)
                                        .fileInfoList(null)
                                        .build());

        BoardInfo boardInfo = boardRepository.findById(1L).get();
        // assertEquals(1, boardInfo.);
        assertEquals("jsan", boardInfo.getUserName());
        assertEquals("title", boardInfo.getTitle());
        assertEquals("content", boardInfo.getContent());
        // assertEquals(2, boardService.read(1L).getData().getFileList().size());

        BoardApiResponse response = BoardApiResponse.builder()
                                                .email("test@naver.com")
                                                .userName("userName")
                                                .title("bdd title")
                                                .content("bdd content")
                                                .boardStatus(BoardStatus.REGISTERED.toString())
                                                .createdDate(LocalDateTime.now())
                                                .createBy("ADMIN")
                                                .fileList(null)
                                                .build();
        // when(boardService.update(request, null, userPrincipal, 1L)).thenReturn(Header.OK(response));

        Header<BoardApiResponse> result = boardService.update(request, null, userPrincipal, 1L);

        assertEquals("userName", result.getData().getUserName());
        assertEquals("bdd title", result.getData().getTitle());
        assertEquals("bdd content", result.getData().getContent());
    }

    @Test
    void delete() {

    }
}
