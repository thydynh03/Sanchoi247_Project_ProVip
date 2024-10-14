package com.example.SanChoi247.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.SanChoi247.model.entity.User;
import com.example.SanChoi247.model.repo.UserRepo;

@Controller
public class SearchController {
    @Autowired
    UserRepo userRepo;

    @GetMapping("/ShowSearchStadium")
    public String showSearchStadium() {
        return "public/searchStadium";
    }

    // Search San theo ten_san
    @PostMapping("/SearchStadiumByTen_San")
    public String searchStadium(@RequestParam("Search") String search, Model model) throws Exception {
        ArrayList<User> userList = userRepo.getAllUser();
        ArrayList<User> findStadium = new ArrayList<>();
        boolean found = false;

        // Loop through the list and find matching stadiums
        for (User san : userList) {
            // Make sure the stadium name is not null before checking
            if (san.getTen_san() != null && san.getTen_san().toLowerCase().contains(search.toLowerCase())) {
                findStadium.add(san);
                found = true;
            }
        }

        // Check if any stadiums were found, if not set an error message
        if (!found) {
            model.addAttribute("error", "Stadium not found");
        }

        model.addAttribute("San", findStadium);
        return "public/searchStadium";
    }

}
