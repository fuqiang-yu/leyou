package com.leyou.page.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {

    @GetMapping("/hello")
    public String hello(Model model){

        model.addAttribute("msg","大家好，我是thymeleaf！！");
//        返回到 一个视图，名字叫 hello
        return "hello";
    }
}
