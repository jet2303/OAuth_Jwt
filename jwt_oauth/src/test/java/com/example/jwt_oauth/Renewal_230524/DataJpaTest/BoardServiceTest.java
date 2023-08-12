package com.example.jwt_oauth.Renewal_230524.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

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
import com.example.jwt_oauth.config.security.token.UserPrincipal;
import com.example.jwt_oauth.domain.board.BoardStatus;
import com.example.jwt_oauth.domain.dto.FileInfoDto;
import com.example.jwt_oauth.payload.Header;
import com.example.jwt_oauth.payload.request.board.CreateBoardRequest;
import com.example.jwt_oauth.payload.response.board.BoardApiResponse;
import com.example.jwt_oauth.repository.board.BoardRepository;
import com.example.jwt_oauth.service.board.BoardService;
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
    @WithAccount(value = "jsan")
    void create() {
        
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

        CreateBoardRequest request1 = new CreateBoardRequest(3L, "title", "email", "userName", "content", BoardStatus.REGISTERED);
        // UserPrincipal userPrincipal = new UserPrincipal(3L, "test2@naver.com", "1234", Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")), "test2");
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        when(boardService.create(request1, null, userPrincipal)).thenReturn(Header.OK(response));

        Header<BoardApiResponse> result = boardService.create(request1, null, userPrincipal);
        
        assertEquals("bdd title", result.getData().getTitle());
        assertEquals("test@naver.com", result.getData().getEmail());
        assertEquals("userName", result.getData().getUserName());
        assertEquals("bdd content", result.getData().getContent());
        assertEquals(BoardStatus.REGISTERED.toString(), result.getData().getBoardStatus());
        // 파일 갯수 0 일경우 null 말고 0으로 리턴
        assertEquals(0, result.getData().getFileList().size());
        
        
    }

    @Test
    void read() {


        log.info("{}", boardService);
        assertNotNull(boardService);
    }

    @Test
    void update() {

    }

    @Test
    void delete() {

    }
}
