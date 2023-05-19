package com.example.jwt_oauth.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.example.jwt_oauth.config.security.token.UserPrincipal;
import com.example.jwt_oauth.domain.board.BoardInfo;
import com.example.jwt_oauth.payload.Header;
import com.example.jwt_oauth.payload.request.board.CreateBoardRequest;
import com.example.jwt_oauth.payload.response.board.BoardApiResponse;
import com.example.jwt_oauth.service.board.BoardService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping(value = "/board")
@RequiredArgsConstructor
@Slf4j
public class BoardController {
    private final BoardService boardService;

    /**
    * @date : 2023-01-20 오후 2:07
    * @author : AJS
    * @Description: 파라미터 boardInfo → BoardApiResponse로 변경
    **/
    // @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    // public String create(@ModelAttribute BoardApiResponse boardApiResponse, 
    //                         @RequestPart(name = "uploadfiles") List<MultipartFile> files){
        
    //     boardService.create(boardApiResponse, files);
    //     return "redirect:/page";
    // }
    @PostMapping(value = "/create")
    public String create(@ModelAttribute CreateBoardRequest request, 
                            @RequestPart(name = "uploadfiles") List<MultipartFile> files
                            , @AuthenticationPrincipal UserPrincipal userPrincipal){
        
        boardService.create(request, files, userPrincipal);
        // boardService.create(request, files, userPrincipal);
        return "redirect:/page";
    }

    @GetMapping(value = "/read/{idx}")
    public ModelAndView boardRead(@PathVariable(name = "idx") Long id){

        Object authentication = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserPrincipal userPrincipal = (UserPrincipal)authentication;

        Header<BoardApiResponse> response = boardService.read(id);
        return new ModelAndView("newBoard/page/view")
                    .addObject("boardDetail", response.getData())
                    .addObject("userName", userPrincipal.getUserName());
    }

    @PutMapping(value = "/update/{idx}")
    public ModelAndView boardUpdate(@ModelAttribute CreateBoardRequest request
                                        ,@RequestPart(name = "uploadfiles") List<MultipartFile> files
                                        ,@PathVariable(name = "idx") Long id){
        Object authentication = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserPrincipal userPrincipal = (UserPrincipal)authentication;
        
        Header<BoardApiResponse> response = boardService.update(request, files, userPrincipal, id);

        return new ModelAndView("newBoard/page/view")
                        .addObject("boardDetail", response.getData())
                        .addObject("userName", userPrincipal.getUserName());
    }
}
