package com.example.jwt_oauth.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.example.jwt_oauth.config.security.token.CurrentUser;
import com.example.jwt_oauth.config.security.token.UserPrincipal;
import com.example.jwt_oauth.domain.board.BoardInfo;
import com.example.jwt_oauth.domain.board.FileInfo;
import com.example.jwt_oauth.domain.dto.BoardInfoDto;
import com.example.jwt_oauth.payload.Header;
import com.example.jwt_oauth.payload.Pagenation;
import com.example.jwt_oauth.payload.request.auth.SignInRequest;
import com.example.jwt_oauth.payload.request.auth.SignUpRequest;
import com.example.jwt_oauth.payload.response.ApiResponse;
import com.example.jwt_oauth.payload.response.board.BoardApiResponse;
import com.example.jwt_oauth.service.board.BoardService;
import com.example.jwt_oauth.service.user.auth.AuthService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PageController {

    private final BoardService boardService;

    @GetMapping(value = "/loginPage")
    public String loginPage(ModelAndView model){
        model.addObject("signInRequest", new SignInRequest());
        return "login";
    }

    @GetMapping(value = "/home")
    public String home(){
        return "home";
    }

    @GetMapping(value="/customSignup")
    public String customSignup(ModelAndView modelAndView){
        modelAndView.addObject("SignUpRequest", new SignUpRequest());
        return "customSignup";
    }
    
    @GetMapping(value = "/main")
    public String main(){
        return "main";
    }

    @GetMapping(value = "/authtest")
    public void authTest(){
        Object authentication = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserPrincipal userPrincipal = (UserPrincipal)authentication;
        System.out.println(userPrincipal.getName() + " " + userPrincipal.getUsername() + " " + userPrincipal.getAuthorities() + " " + userPrincipal.getPassword());
        System.out.println(userPrincipal.toString());
    }

    // @GetMapping(value = "/menutest")
    // public String menutest(){
    //     return "menu/index";
    // }

    /**
    * @date : 2023-03-16 오후 2:07
    * @author : AJS
    * @Description: view 리뉴얼로 이전에 쓰던 list, write 페이지 주석처리
    **/

    @GetMapping("/page")
    public ModelAndView findPage(@PageableDefault(size = 10, page = 0, sort = "id", direction = Sort.Direction.DESC) Pageable pageable){
    
        Header<List<BoardApiResponse>> result = boardService.search(pageable);
        List<BoardApiResponse> boardApiResponses = result.getData();
        Pagenation pagenation = result.getPagenation();
        return new ModelAndView("/newBoard/page/list")
                            .addObject("boardList", boardApiResponses)
                            .addObject("pagenation", pagenation);
    }

    // new Page
    @GetMapping(value = "/write")
    public ModelAndView write(){
        Object authentication = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserPrincipal userPrincipal = (UserPrincipal)authentication;

        return new ModelAndView("/newBoard/page/write")
                    .addObject("boardApiResponse", new BoardApiResponse())
                    .addObject("uploadfiles", new FileInfo())
                    .addObject("userName", userPrincipal.getUserName());
    }

    

    

}
