package com.example.jwt_oauth.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.example.jwt_oauth.domain.board.BoardInfo;
import com.example.jwt_oauth.payload.Header;
import com.example.jwt_oauth.payload.response.board.BoardApiResponse;
import com.example.jwt_oauth.service.board.BoardService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping(value = "/board")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    @PostMapping(value = "/create")
    public String create(@RequestBody BoardInfo boardInfo, @RequestPart(name = "image") List<MultipartFile> files){
        boardService.create(boardInfo, files);
        return "redirect:page/list";
    }

    @GetMapping(value = "/read/{idx}")
    public Header<BoardApiResponse> boardRead(@PathVariable(name = "idx") Long id){
        return boardService.read(id);
    }
}
