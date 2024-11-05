package com.example.SanChoi247.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @GetMapping(value = "/admin/dashboard")
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
    @GetMapping("admin/pendingRequests")
public ResponseEntity<List<User>> getPendingRequests() {
    try {
        List<User> pendingUsers = userRepo.getUsersByStatus(1); // Trạng thái 1 là "Đang chờ xét duyệt"
        return ResponseEntity.ok(pendingUsers);
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

    // Trả về trang HTML approveFieldOwner.html
    @GetMapping("admin/approveFieldOwner")
    public String showApprovalPage() {
        return "admin/approveFieldOwner"; // Đây là file approveFieldOwner.html trong thư mục templates/admin
    }

    // Phương thức POST để duyệt hoặc từ chối yêu cầu làm Field Owner
    @PostMapping("admin/approveFieldOwner")
public ResponseEntity<String> approveFieldOwner(@RequestParam int uid, @RequestParam boolean isApproved) {
    String statusMessage;
    try {
        userRepo.approveFieldOwnerRequest(uid, isApproved);
        User user = userRepo.getUserById(uid); // Lấy thông tin người dùng từ uid
        String subject, content;

        // Kiểm tra nếu duyệt thành công hoặc từ chối để gửi email với nội dung tương ứng
        if (isApproved) {
            statusMessage = "Trạng thái: Đã chấp nhận";
            subject = "Chúc mừng bạn đã trở thành Field Owner";
            content = "<html><body>" +
                      "<p>Chúc mừng <strong>" + user.getName() + "</strong>!</p>" +
                      "<p>Bạn đã trở thành Field Owner, giờ bạn có thể đăng ký sân của bạn trong thanh menu của bạn.</p>" +
                      "<p>Chúc bạn thành công với việc quản lý sân!</p>" +
                      "</body></html>";
        } else {
            statusMessage = "Trạng thái: Từ chối";
            subject = "Yêu cầu Field Owner không được phê duyệt";
            content = "<html><body>" +
                      "<p>Xin chào <strong>" + user.getName() + "</strong>,</p>" +
                      "<p>Yêu cầu trở thành Field Owner của bạn không được phê duyệt. Vui lòng cập nhật đủ thông tin cần thiết để chúng tôi có thể xem xét lại yêu cầu của bạn.</p>" +
                      "<p>Cảm ơn bạn đã quan tâm đến dịch vụ của chúng tôi!</p>" +
                      "</body></html>";
        }

        // Gửi thông báo qua WebSocket đến client
        messagingTemplate.convertAndSend("/topic/statusUpdate", statusMessage);

        // Gửi email thông báo
        emailService.sendEmail(user.getEmail(), subject, content);

        return ResponseEntity.ok(statusMessage);
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Approval failed.");
    }
}
@GetMapping("/admin/approveField")
    public String showApproveFieldPage(Model model) throws Exception {
        List<User> pendingFields = userRepo.findUsersByStatus(3); // Get fields with status 3 (pending approval)
        model.addAttribute("pendingFields", pendingFields);
        return "admin/approveField";
    }

@PostMapping("/admin/approveField")
public String approveField(@RequestParam("uid") int uid, @RequestParam("approve") boolean approve) throws Exception {
    User user = userRepo.getUserById(uid); // Fetch user details to get their email

    if (approve) {
        userRepo.updateFieldStatus(uid, 4); // Approve field by setting status to 4
        // Send approval email
        String subject = "Congratulations! Your Field has been Approved on SanChoi247";
        String body = "Dear " + user.getName() + ",\n\n" +
                "We are pleased to inform you that your field submission, \"" + user.getTen_san() + "\", located at \"" + user.getAddress() + "\", " +
                "has been successfully approved by the SanChoi247 team.\n\n" +
                "This means that your field is now officially listed on our platform, and players can start booking it! " +
                "We believe your field will provide an excellent space for sports enthusiasts in the community to play and enjoy their favorite games.\n\n" +
                "Here’s what you can do next:\n" +
                "- Visit your field listing to see how it appears to potential players.\n" +
                "- Ensure all details, including images and location, are up-to-date.\n" +
                "- Monitor bookings and respond promptly to any inquiries to build trust and good relationships with players.\n\n" +
                "If you have any questions, please don't hesitate to reach out to our support team.\n\n" +
                "Thank you for being a part of SanChoi247!\n\n" +
                "Best Regards,\n" +
                "SanChoi247 Team";

        emailService.sendEmail(user.getEmail(), subject, body);
    } else {
        userRepo.updateFieldStatus(uid, 0); // Reject field by setting status to 0
        // Send rejection email
        String subject = "Field Submission Update on SanChoi247";
        String body = "Dear " + user.getName() + ",\n\n" +
                "We regret to inform you that your field submission, \"" + user.getTen_san() + "\", located at \"" + user.getAddress() + "\", " +
                "did not meet our requirements for approval at this time.\n\n" +
                "Here are a few common reasons fields may not be approved:\n" +
                "- Incomplete or inaccurate information provided.\n" +
                "- Photos that do not clearly represent the field or its amenities.\n" +
                "- Issues with the field's location or accessibility.\n\n" +
                "We encourage you to review your submission, make any necessary adjustments, and resubmit if you believe the field meets our guidelines. " +
                "You can always reach out to our support team if you need further clarification or assistance.\n\n" +
                "Thank you for your interest in SanChoi247, and we hope to see your field listed soon.\n\n" +
                "Best Regards,\n" +
                "SanChoi247 Team";

        emailService.sendEmail(user.getEmail(), subject, body);
    }
    return "redirect:/admin/approveField";
}

}
