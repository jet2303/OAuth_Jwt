package com.example.jwt_oauth.controller;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CustomErrorController implements ErrorController{
    
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, ModelAndView modelAndView){
        Object attribute = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        
        modelAndView.addObject("code", attribute.toString());
        modelAndView.addObject("msg", HttpStatus.valueOf(Integer.valueOf(attribute.toString())));

        return "error";
    }

}
