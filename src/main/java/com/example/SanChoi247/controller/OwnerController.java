package com.example.SanChoi247.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.SanChoi247.model.entity.LoaiSan;
import com.example.SanChoi247.model.entity.San;
import com.example.SanChoi247.model.entity.Size;
import com.example.SanChoi247.model.entity.User;
import com.example.SanChoi247.model.repo.SanRepo;
import com.example.SanChoi247.model.service.FileUploadService;

import jakarta.servlet.http.HttpSession;



@Controller
public class OwnerController {
    @Autowired
    private FileUploadService fileUpload;
    @Autowired
SanRepo sanRepo;
@GetMapping("/owner/addSmallField")
public String showAddSmallFieldForm(HttpSession session, Model model) throws Exception {
    User user = (User) session.getAttribute("UserAfterLogin");

    if (user == null) {
        return "auth/login"; // Redirect to login if user is not logged in
    }
    return "owner/addSmallField"; // Direct to the form or message page
}
@PostMapping("/owner/addSmallField")
public String addSmallField(
        @RequestParam("loai_san_id") int loaiSanId,
        @RequestParam("vi_tri_san") String viTriSan,
        @RequestParam("size_id") int sizeId,
        @RequestParam("img") MultipartFile imgFile,
        HttpSession session, Model model) {

    // Lấy thông tin người dùng từ session
    User user = (User) session.getAttribute("UserAfterLogin");

    if (user == null) {
        return "auth/login"; // Chuyển hướng đến trang đăng nhập nếu chưa đăng nhập
    }

    try {
        // Tải lên ảnh và lấy URL
        String imgURL = fileUpload.uploadFile(imgFile);

        // Tạo đối tượng mới cho sân
        San newField = new San();
        newField.setUser(user);
        newField.setLoaiSan(new LoaiSan(loaiSanId));
        newField.setVi_tri_san(viTriSan);
        newField.setSize(new Size(sizeId));
        newField.setImg(imgURL);
        newField.setIs_approve(0); // Trạng thái đang chờ phê duyệt
        newField.setEyeview(0);

        // Gọi phương thức addNewSan để lưu dữ liệu vào cơ sở dữ liệu
        sanRepo.addNewSan(newField);

        model.addAttribute("message", "Small field added successfully and is pending approval.");
        model.addAttribute("status", "success");
    } catch (Exception e) {
        e.printStackTrace();
        model.addAttribute("message", "Failed to add the field.");
        model.addAttribute("status", "error");
    }

    return "redirect:/";
}



}
