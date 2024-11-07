package com.example.SanChoi247.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BlogController {
    @GetMapping("/ShowBlog")
    public String showBlog() {
        return "user/blog";
    }
}
