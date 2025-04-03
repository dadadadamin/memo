package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

//HTML페이지를 반환하는 컨트롤러
//@Conroller 어노테이션이 붙어 있으며, templates 폴더의 *.html을 렌더링할 가능성이 높은 파일 
//@GetMapping을 사용하여 특정 URL 요청 시 View(HTML)를 응답.
@Controller
public class UserViewController {
    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/signup")
    public String signup(){
        return "signup";
    }
}
