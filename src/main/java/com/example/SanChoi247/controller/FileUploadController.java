package com.example.SanChoi247.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.SanChoi247.model.entity.User;
import com.example.SanChoi247.model.repo.UserRepo;
import com.example.SanChoi247.service.FileUpload;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class FileUploadController {
    @Autowired
    FileUpload fileUpload;
    @Autowired
    UserRepo userRepo;

    // @RequestMapping("/")
    // public String home() {
    // return "home";
    // }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("image") MultipartFile multipartFile, Model model, HttpSession httpSession)
            throws Exception {
        String imageURL = fileUpload.uploadFile(multipartFile);
        User user = (User) httpSession.getAttribute("UserAfterLogin");
        userRepo.editAvatarUser(imageURL, user.getUid());
        return "redirect:/ShowEditProfile";
    }
}
