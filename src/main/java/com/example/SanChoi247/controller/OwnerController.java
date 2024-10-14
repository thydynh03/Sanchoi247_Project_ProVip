package com.example.SanChoi247.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OwnerController {
    @GetMapping("/ShowForOwners")
    public String showForOwners() {
        return "public/forOwners";
    }
}
