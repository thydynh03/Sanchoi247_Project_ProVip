package com.example.SanChoi247.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.SanChoi247.model.entity.San;
import com.example.SanChoi247.model.entity.User;
import com.example.SanChoi247.model.repo.UserRepo;

@Controller
public class SearchController {
    @Autowired
    UserRepo userRepo;

    public static String removeAccent(String text) {
        String normalized = java.text.Normalizer.normalize(text, java.text.Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "");
    }

    // phuong thuc search
    @PostMapping("/SearchSanByTenSan")
    public String searchSanByTenSan(@RequestParam("Search") String Search, Model model) throws Exception {
        ArrayList<User> userList = userRepo.getAllUser();
        ArrayList<User> findSan = new ArrayList<>();

        // Xử lý từ khóa tìm kiếm
        String searchNormalized = removeAccent(Search).toLowerCase();

        for (User tenSan : userList) {
            if (tenSan.getTen_san() != null) {
                // Xử lý tên sân và loại bỏ dấu
                String tenSanNormalized = removeAccent(tenSan.getTen_san()).toLowerCase();

                // So sánh tên sân đã loại bỏ dấu
                if (tenSanNormalized.contains(searchNormalized)) {
                    findSan.add(tenSan);
                }
            }
        }

        if (findSan.isEmpty()) {
            model.addAttribute("message", "not found");
        } else {
            model.addAttribute("userList", findSan);
        }

        return "search/searchResult";
    }

    // --------------------------------------------------------------------------------------------------------//
    // Show trang search

    @GetMapping("/ShowSearch")
    public String showSearch() {
        return "search/searchResult";
    }
}
