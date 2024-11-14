package com.example.SanChoi247.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NavbarController {
    @GetMapping("/ShowIntroduction")
    public String showIntroduction() {
        return "public/navbar/introduction";
    }

    @GetMapping("/ShowForOwners")
    public String showForOwners() {
        return "public/navbar/forOwners";
    }
}
