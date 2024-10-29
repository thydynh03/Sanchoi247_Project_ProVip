package com.example.SanChoi247.controller;

import java.sql.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.SanChoi247.model.entity.User;
import com.example.SanChoi247.model.repo.UserRepo;

import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {
    @Autowired
    UserRepo userRepo;

    // @GetMapping("/ShowEditProfile")
    // public String showOrderByUserId(HttpSession httpSession, Model model) throws
    // Exception {
    // User user = (User) httpSession.getAttribute("UserAfterLogin");
    // User user1 = userRepo.getUserById(user.getUid());
    // model.addAttribute("user", user1);
    // return "user/editProfile";
    // }

    // @PostMapping("/EditProfile")
    // public String editProfile(@RequestParam("uid") int uid,
    // @RequestParam("Name") String name,
    // @RequestParam("Dob") @DateTimeFormat(pattern = "yyyy-MM-dd") java.util.Date
    // dob,
    // @RequestParam("Gender") char gender,
    // @RequestParam("Phone") String phone,
    // @RequestParam("Email") String email,
    // @RequestParam("Username") String username,
    // @RequestParam("Password") String password,
    // @RequestParam("Avatar") String avatar,
    // @RequestParam("Ten_san") String ten_san,
    // @RequestParam("Address") String address,
    // @RequestParam("img_san1") String img_san1,
    // @RequestParam("img_san2") String img_san2,
    // @RequestParam("img_san3") String img_san3,
    // @RequestParam("img_san4") String img_san4,
    // @RequestParam("img_san5") String img_san5,
    // @RequestParam("Status") int status,
    // @RequestParam("Role") char role,
    // Model model) throws Exception {
    // try {
    // java.sql.Date sqlDob = new java.sql.Date(dob.getTime());

    // User user = new User(uid, name, sqlDob, gender, phone, email, username,
    // password, avatar, ten_san, address,
    // img_san1, img_san2, img_san3, img_san4, img_san5, status, role);

    // userRepo.updateUserById(user);
    // model.addAttribute("message", "Profile updated successfully!");
    // } catch (Exception e) {
    // model.addAttribute("message", "Error updating profile!");
    // e.printStackTrace();
    // }
    // return "user/editProfile";
    // }

    @GetMapping("/ShowEditProfile")
    public String showEditProfile(HttpSession httpSession, Model model) throws Exception {
        User user = (User) httpSession.getAttribute("UserAfterLogin");
        if (user == null) {
            return "redirect:/login"; // Chuyển hướng về trang đăng nhập nếu chưa đăng nhập
        }
        User newU = userRepo.getUserById(user.getUid());
        model.addAttribute("user", newU);
        return "user/editProfile"; // Trả về trang chỉnh sửa hồ sơ
    }

    @PostMapping(value = "/EditProfile") // Them thuoc tinh cho organizer
    public String editUser(@RequestParam("name") String nameInput,
            @RequestParam("phone") String phoneInput,
            @RequestParam("email") String emailInput,
            @RequestParam("dob") Date dobInput,
            @RequestParam("gender") char genderInput,
            @RequestParam(value = "ten_san", required = false) String ten_sanInput,
            @RequestParam(value = "address", required = false) String addressInput,
            @RequestParam(value = "img_san1", required = false) String img_san1Input,
            @RequestParam(value = "img_san2", required = false) String img_san2Input,
            @RequestParam(value = "img_san3", required = false) String img_san3Input,
            @RequestParam(value = "img_san4", required = false) String img_san4Input,
            @RequestParam(value = "img_san5", required = false) String img_san5Input,
            Model model,
            HttpSession httpSession) throws Exception {
        try {
            User activeUser = (User) httpSession.getAttribute("UserAfterLogin");

            // Update common user attributes
            userRepo.editProfile(nameInput, dobInput, genderInput, phoneInput,
                    emailInput, activeUser.getUid());
            activeUser.setName(nameInput);
            activeUser.setPhone(phoneInput);
            activeUser.setEmail(emailInput);
            activeUser.setDob(dobInput);
            activeUser.setGender(genderInput);
            userRepo.save(activeUser);

            // Update if the user is an owner
            if (activeUser.getRole() == 'C') {
                User user = userRepo.getUserById(activeUser.getUid());
                if (ten_sanInput != null)
                    user.setTen_san(ten_sanInput);
                if (addressInput != null)
                    user.setAddress(addressInput);
                if (img_san1Input != null)
                    user.setImg_san1(img_san1Input);
                if (img_san2Input != null)
                    user.setImg_san2(img_san2Input);
                if (img_san3Input != null)
                    user.setImg_san3(img_san3Input);
                if (img_san4Input != null)
                    user.setImg_san4(img_san4Input);
                if (img_san5Input != null)
                    user.setImg_san5(img_san5Input);

                userRepo.save(user);
                httpSession.setAttribute("activeOwner", user);
            }

            // Fetch the updated user and update session
            activeUser = userRepo.getUserById(activeUser.getUid());
            httpSession.setAttribute("UserAfterLogin", activeUser);

            // Add success message to model
            model.addAttribute("message", "Edited successfully!");

            return "user/editProfile"; // Return to the edit profile page

        } catch (Exception e) {
            // In case of any exception, add an error message to the model
            model.addAttribute("errorMessage", "An error occurred while editing your profile.");
            return "user/editProfile"; // Return to the edit profile page with error message
        }
    }

    // ---------------------------------------------------------------------------------//

    @GetMapping("/ShowChangePassword")
    public String showChangePasswordForm() {
        return "user/changePassword";
    }

    @PostMapping("/changePassword")
    public String changePassword(
            @RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            HttpSession session,
            Model model) {
        try {
            User user = (User) session.getAttribute("UserAfterLogin");

            // Kiểm tra xem mật khẩu cũ có đúng không
            if (!user.getPassword().equals(oldPassword)) {
                model.addAttribute("error", "Old password is incorrect.");
            } else if (newPassword.equals(oldPassword)) {
                model.addAttribute("error", "New password cannot be the same as the old password.");
            } else if (!newPassword.equals(confirmPassword)) {
                model.addAttribute("error", "New password and confirm password do not match.");
            } else {
                userRepo.updatePassword(user.getUid(), newPassword);
                model.addAttribute("message", "Password updated successfully!");
                return "redirect:/Logout"; // Chuyển hướng sau khi thay đổi thành công
            }
        } catch (Exception e) {
            model.addAttribute("error", "Error updating password.");
            e.printStackTrace();
        }
        return "user/changePassword";
    }

    // ---------------------------------------------------------------------------------//

    @GetMapping("/VerifyEmail")
    public String showVerifyEmailPage(Model model, HttpSession session) throws Exception {
        User user = (User) session.getAttribute("UserAfterLogin");
        if (user == null || user.isVerified()) {
            return "redirect:/"; // Redirect if not applicable
        }
        return "auth/verifyEmail";
    }

       @GetMapping("user/requestFieldOwner")
    public String requestForm() {
        return "user/requestFieldOwner"; // Trả về trang HTML
    }

    // Phương thức POST để xử lý yêu cầu phê duyệt field owner
    @PostMapping("user/requestFieldOwner")
public ResponseEntity<String> requestFieldOwner(HttpSession httpSession) {
    try {
        User user = (User) httpSession.getAttribute("UserAfterLogin"); // Lấy user từ session
        int uid = user.getUid(); // Lấy `uid` từ user đăng nhập

        userRepo.requestFieldOwner(uid); // Gửi yêu cầu phê duyệt
        return ResponseEntity.ok("Request submitted. Waiting for admin approval.");
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Request failed.");
    }
}




    // Phương thức GET để trả về trạng thái người dùng
    @GetMapping("user/getStatus")
public ResponseEntity<String> getUserStatus(HttpSession httpSession) {
    try {
        // Lấy user từ session
        User user = (User) httpSession.getAttribute("UserAfterLogin");
        
        // Kiểm tra nếu người dùng chưa đăng nhập
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in.");
        }

        int status = user.getStatus();
        String statusMessage;
        
        switch (status) {
            case 0:
                statusMessage = "Đang chờ xét duyệt";
            case 1:
                statusMessage = "Đang chờ xét duyệt";
                break;
            case 2:
                statusMessage = "Duyệt thành công";
                break;
            default:
                statusMessage = "Trạng thái không xác định";
        }
        
        return ResponseEntity.ok(statusMessage);
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Lỗi khi lấy trạng thái.");



        
    }
}
}