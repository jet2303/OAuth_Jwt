package com.example.jwt_oauth.payload.request.board;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.jwt_oauth.domain.board.BoardStatus;
import com.example.jwt_oauth.payload.response.board.BoardApiResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Data
@Getter
@Setter
@AllArgsConstructor
public class CreateBoardRequest {
    
    // private Long id;

    private String title;

    private String userName;

    private String content;

    private BoardStatus boardStatus = BoardStatus.REGISTERED;

}
