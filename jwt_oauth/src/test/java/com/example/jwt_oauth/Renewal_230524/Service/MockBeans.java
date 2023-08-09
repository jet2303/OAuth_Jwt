package com.example.jwt_oauth.Renewal_230524.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.jwt_oauth.repository.board.BoardRepository;
import com.example.jwt_oauth.repository.board.FileInfoRepository;
import com.example.jwt_oauth.repository.user.UserRepository;
import com.example.jwt_oauth.service.board.BoardService;
import com.example.jwt_oauth.service.user.UserService;
import com.example.jwt_oauth.service.user.auth.AuthService;

public abstract class   MockBeans {

    @Autowired
    protected BoardService boardService;

    @Autowired
    protected AuthService authService;

    @Autowired
    protected MockHttpServletResponse mockHttpServletResponse;

    @Autowired
    protected BoardRepository boardRepository;

    @Autowired
    protected FileInfoRepository fileInfoRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected UserService userService;
}
