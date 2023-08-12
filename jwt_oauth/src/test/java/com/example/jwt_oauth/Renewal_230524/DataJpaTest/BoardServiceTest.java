package com.example.jwt_oauth.Renewal_230524.DataJpaTest;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.InvocationInterceptor.Invocation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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

    // @Autowired
    // private TestEntityManager testEntityManager;

    // @Autowired
    // private BoardRepository boardRepository;

    @Mock
    private BoardService boardService;

    @Test
    // @WithAccount(value = "jsan")
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

        CreateBoardRequest request = new CreateBoardRequest(3L, "title", "email", "userName", "content",
                BoardStatus.REGISTERED);
        UserPrincipal userPrincipal = new UserPrincipal(3L, "test2@naver.com",
                "1234", Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")),
                "test2");

        // when
        when(boardService.create(request, null, userPrincipal)).thenReturn(Header.OK(response));

        Header<BoardApiResponse> result = boardService.create(request, null, userPrincipal);

        // then
        assertEquals("bdd title", result.getData().getTitle());
        assertEquals("test@naver.com", result.getData().getEmail());
        assertEquals("userName", result.getData().getUserName());
        assertEquals("bdd content", result.getData().getContent());
        assertEquals(BoardStatus.REGISTERED.toString(), result.getData().getBoardStatus());
        assertNull(result.getData().getFileList());

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

    }

    @Test
    void delete() {

    }
}
