package com.example.SanChoi247.controller;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.Comparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.SanChoi247.model.dto.AdminStatistics;
import com.example.SanChoi247.model.dto.TableAdminStatistics;
import com.example.SanChoi247.model.entity.User;
import com.example.SanChoi247.model.repo.AdminRepo;
import com.example.SanChoi247.model.repo.UserRepo;
import com.example.SanChoi247.model.service.EmailService;

import jakarta.servlet.http.HttpSession;

@Controller
public class AdminController {
    @Autowired
    AdminRepo adminRepo;
    @Autowired
    UserRepo userRepo;
    @Autowired
    EmailService emailService;

    @GetMapping(value = "admin/dashboard")
    public String dashboardPage(Model model) throws Exception {
        AdminStatistics statistics = adminRepo.getStatisticsForAdmin();
        List<TableAdminStatistics> stadiumRevenues = adminRepo.getFieldRevenues();
        List<TableAdminStatistics> sortedStadiumRevenues = stadiumRevenues.stream()
                .sorted(Comparator.comparingDouble(TableAdminStatistics::getRevenue).reversed())
                .collect(Collectors.toList());

        model.addAttribute("statisticsOfAdmin", statistics);
        model.addAttribute("stadiumRevenues", sortedStadiumRevenues);

        return "admin/dashboard";
    }

    @GetMapping(value = ("/ViewAllUser"))
    public String allUserPage(Model model) throws Exception {
        ArrayList<User> users = adminRepo.getAllUser();
        model.addAttribute("users", users);
        model.addAttribute("status", "approve");
        return "admin/users/view";
    }

    // @PostMapping(value = "/lockUser")
    // public String lockUsers(@RequestParam("uid") int id, @RequestParam("role")
    // char role, Model model)
    // throws Exception {
    // adminRepo.lockUser(id, role);
    // ArrayList<User> users = adminRepo.getAllUser();
    // model.addAttribute("users", users);
    // model.addAttribute("status", "approve");
    // return "admin/users/view";
    // }

    @PostMapping(value = "/lockUser")
    public String lockUsers(@RequestParam("uid") int id, @RequestParam("role") char role, Model model)
            throws Exception {
        adminRepo.lockUser(id, role);
        User user = userRepo.getUserById(id);
        String subject = "Your Sanchoi247 Account has been Locked";
        String content = "<html><body style='font-family: Arial, sans-serif;'>" +
                "<div style='background-color: #f8f9fa; padding: 20px; border-radius: 10px; max-width: 600px; margin: auto;'>"
                +
                "<h1 style='color: #007bff; text-align: center;'>Sanchoi247 Account</h1>" +
                "<p style='font-size: 16px; color: #333;'>Hello <strong>" + user.getName() + "</strong>,</p>" +
                "<p style='font-size: 16px; color: #333;'>We regret to inform you that your account has been <strong style='color: #ff0000;'>locked</strong> due to suspicious activity.</p>"
                +
                "<p style='font-size: 16px; color: #333;'>Please contact our admin at <a href='mailto:247sanchoi@gmail.com' style='color: #007bff; text-decoration: none;'>247sanchoi@gmail.com</a> for further assistance.</p>"
                +
                "<p style='font-size: 16px; color: #333;'>Thank you for using Sanchoi247!</p>" +
                "<p style='font-size: 14px; color: #777;'>This is an automated email, please do not reply to this email.</p>"
                +
                "<br>" +
                "<p style='font-size: 12px; color: #555; text-align: center;'>(c) 2024 Sanchoi247. All rights reserved</p>"
                +
                "</div>" +
                "</body></html>";

        // Gửi email thông báo
        emailService.sendEmail(user.getEmail(), subject, content);
        ArrayList<User> users = adminRepo.getAllUser();
        model.addAttribute("users", users);
        model.addAttribute("status", "approve");

        return "admin/users/view";
    }

    @PostMapping(value = "/unlockUser")
    public String unlockUsers(@RequestParam("uid") int id, @RequestParam("role") char role, Model model)
            throws Exception {
        adminRepo.unlockUser(id, role);
        User user = userRepo.getUserById(id);
        String subject = "Your Sanchoi247 Account has been Unlocked";
        String content = "<html><body style='font-family: Arial, sans-serif;'>" +
                "<div style='background-color: #f8f9fa; padding: 20px; border-radius: 10px; max-width: 600px; margin: auto;'>"
                +
                "<h1 style='color: #28a745; text-align: center;'>Sanchoi247 Account</h1>" +
                "<p style='font-size: 16px; color: #333;'>Hello <strong>" + user.getName() + "</strong>,</p>" +
                "<p style='font-size: 16px; color: #333;'>We are pleased to inform you that your account has been <strong style='color: #28a745;'>unlocked</strong> and you can now access all features of Sanchoi247.</p>"
                +
                "<p style='font-size: 16px; color: #333;'>If you have any questions or need further assistance, please contact our support team at <a href='mailto:247sanchoi@gmail.com' style='color: #007bff; text-decoration: none;'>247sanchoi@gmail.com</a>.</p>"
                +
                "<p style='font-size: 16px; color: #333;'>Thank you for using Sanchoi247!</p>" +
                "<p style='font-size: 14px; color: #777;'>This is an automated email, please do not reply to this email.</p>"
                +
                "<br>" +
                "<p style='font-size: 12px; color: #555; text-align: center;'>(c) 2024 Sanchoi247. All rights reserved</p>"
                +
                "</div>" +
                "</body></html>";

        // Gửi email thông báo
        emailService.sendEmail(user.getEmail(), subject, content);
        ArrayList<User> banners = adminRepo.getAllBanner();
        model.addAttribute("status", "reject");
        model.addAttribute("users", banners);
        return "admin/users/view";
    }

    @GetMapping(value = ("/ViewAllBanner"))
    public String allBannerPage(Model model) throws Exception {
        ArrayList<User> banners = adminRepo.getAllBanner();
        model.addAttribute("status", "reject");
        model.addAttribute("users", banners);

        return "admin/users/view";
    }

    @GetMapping(value = ("/ViewActiveOwner"))
    public String allActiveOrganizer(Model model) throws Exception {
        ArrayList<User> owner = adminRepo.getAllActiveOwner();
        model.addAttribute("owner", owner);
        return "admin/users/owners-active";
    }

    @GetMapping(value = ("/ViewAllStadiumsActive"))
    public String allStadiumsOngoingPage(Model model) throws Exception {
        List<User> stadiums = adminRepo.getAllActiveOwner();
        model.addAttribute("stadiums", stadiums);
        return "admin/san/stadiums-active";
    }

    @GetMapping(value = "/searchEvents")
    public String searchEvents(@RequestParam("query") String query, Model model) throws Exception {
        List<User> user = userRepo.searchStadium(query);
        model.addAttribute("stadiums", user);
        return "admin/san/stadiums-active";
    }

    @PostMapping(value = "/stadiumEditPageAdmin")
    public String eventEditPage(@RequestParam("stadiumId") int stadiumId, Model model, HttpSession httpSession)
            throws Exception {
        httpSession.setAttribute("stadiumIdEdit", stadiumId);
        User user = userRepo.getUserById(stadiumId);
        model.addAttribute("stadiumEdit", user);
        return "admin/san/editStadiumAdmin";
    }

    @PostMapping(value = "/sendMailToActiveOwner")
    public String SendMailToAllActiveOrganizer(
            @RequestParam("Title") String title,
            @RequestParam("content") String content,
            @RequestParam("ownerEmails") List<String> ownerEmails,
            Model model) throws Exception {

        // Tạo tiêu đề email
        String subject = title;

        // Tạo nội dung email với các mục cần sửa
        String emailContent = "<html><body style='font-family: sans-serif;'>" +
                content
                +

                "<p style='font-size: 16px;'>Thank you for using SanChoi247!</p>" +
                "<p style='font-size: 14px; color: #777;'>This is an automated email, please do not reply to this email.</p>"
                +
                "<br>" +
                "<p style='font-size: 12px; color: #555;'>(c) 2024 SanChoi247. All rights reserved</p>" +
                "</body></html>";

        // Gửi email thông báo tới từng người dùng
        for (String email : ownerEmails) {
            emailService.sendEmail(email, subject, emailContent);
        }
        return "redirect:/ViewActiveOwner";
    }
}
