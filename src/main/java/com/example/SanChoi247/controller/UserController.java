package com.example.SanChoi247.controller;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.SanChoi247.model.entity.User;
import com.example.SanChoi247.model.repo.BookingRepo;
import com.example.SanChoi247.model.repo.LoginRepo;
import com.example.SanChoi247.model.repo.UserRepo;
import com.example.SanChoi247.model.service.FileUploadService;

import jakarta.servlet.http.HttpSession;
@Controller
public class UserController {
    
    @Autowired
    UserRepo userRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    LoginRepo loginRepo;
    @Autowired
    BookingRepo bookingRepo;
    @Autowired
    private FileUploadService fileUpload;

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
                //httpSession.setAttribute("activeOwner", user);
            }

            // Fetch the updated user and update session
            activeUser = userRepo.getUserById(activeUser.getUid());
            httpSession.setAttribute("UserAfterLogin", activeUser);

            // Add success message to model
            model.addAttribute("message", "Edited successfully!");

            return "redirect:/ShowEditProfile"; // Return to the edit profile page

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

            // Kiểm tra mật khẩu cũ với repository
            if (!loginRepo.checkOldPassword(user.getUid(), oldPassword)) {
                model.addAttribute("error", "Old password is incorrect.");
            } else if (newPassword.equals(oldPassword)) {
                model.addAttribute("error", "New password cannot be the same as the old password.");
            } else if (!newPassword.equals(confirmPassword)) {
                model.addAttribute("error", "New password and confirm password do not match.");
            } else {
                // Mã hóa mật khẩu mới trước khi cập nhật vào cơ sở dữ liệu
                String encodedNewPassword = passwordEncoder.encode(newPassword);
                userRepo.updatePassword(user.getUid(), encodedNewPassword);
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
    @GetMapping("/user/requestFieldOwner")
    public String requestForm() {
        return "user/requestFieldOwner"; // Trả về trang HTML
    }

    // Phương thức POST để xử lý yêu cầu phê duyệt field owner
    @PostMapping("/user/requestFieldOwner")
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
@GetMapping("user/getUserRole")
public ResponseEntity<Map<String, String>> getUserRole(HttpSession httpSession) {
    Map<String, String> response = new HashMap<>();
    User user = (User) httpSession.getAttribute("UserAfterLogin");
    
    if (user == null) {
        response.put("role", "GUEST");
    } else {
        response.put("role", String.valueOf(user.getRole()));
    }
    return ResponseEntity.ok(response);
}
@GetMapping("/owner/registerField")
public String showRegisterFieldForm(HttpSession session, Model model) {
    User user = (User) session.getAttribute("UserAfterLogin");
    
    // Kiểm tra xem user có tồn tại và đã đăng ký sân hay chưa
    if (user != null && user.getTen_san() != null && user.getStatus() == 4) {
        model.addAttribute("message", "Bạn đã đăng kí sân.");
        model.addAttribute("status", 3);
        return "owner/registerField";
    }
    
    return "owner/registerField"; // Hiển thị form đăng ký nếu chưa đăng ký
}

    // ---------------------------------------------------------------------------------//
    @PostMapping("/owner/registerField")
public String registerField(
        @RequestParam("ten_san") String tenSan,
        @RequestParam("address") String address,
        @RequestParam("img_san1") MultipartFile imgSan1,
        @RequestParam("img_san2") MultipartFile imgSan2,
        @RequestParam("img_san3") MultipartFile imgSan3,
        @RequestParam("img_san4") MultipartFile imgSan4,
        @RequestParam("img_san5") MultipartFile imgSan5,
        HttpSession session, Model model) throws Exception {
    
    User user = (User) session.getAttribute("UserAfterLogin");
    
    // Kiểm tra xem user có tồn tại trong session không
    if (user == null) {
        return "auth/login"; // Nếu không, chuyển hướng tới trang đăng nhập
    }

    // Upload từng ảnh và lấy đường dẫn ảnh
    String imgSan1URL = fileUpload.uploadFile(imgSan1);
    String imgSan2URL = fileUpload.uploadFile(imgSan2);
    String imgSan3URL = fileUpload.uploadFile(imgSan3);
    String imgSan4URL = fileUpload.uploadFile(imgSan4);
    String imgSan5URL = fileUpload.uploadFile(imgSan5);

    // Cập nhật thông tin sân và lưu đường dẫn ảnh vào cơ sở dữ liệu
    user.setTen_san(tenSan);
    user.setAddress(address);
    user.setStatus(3); // Đặt trạng thái là 3: Chờ duyệt
    userRepo.saveFieldInfo(user, imgSan1URL, imgSan2URL, imgSan3URL, imgSan4URL, imgSan5URL);
    
    // Truyền thông báo trạng thái vào model để hiển thị
    model.addAttribute("message", "Đã gửi thành công. Hãy chờ admin duyệt thông tin của bạn.");
    model.addAttribute("status", user.getStatus());

    // Trả về cùng trang `registerField.html` với thông báo trạng thái
    return "owner/registerField";
}


}