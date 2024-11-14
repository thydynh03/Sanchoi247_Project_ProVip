package com.example.SanChoi247.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileUploadService {

    private final String uploadDir = "upload"; // Thư mục lưu ảnh // Thay thế bằng thư mục lưu trữ ảnh của bạn

    public String uploadFile(MultipartFile file) throws IOException {
        // Tạo thư mục "upload" nếu chưa tồn tại
        File dir = new File("upload");
        if (!dir.exists()) dir.mkdirs();
    
        // Đặt tên file duy nhất để tránh trùng
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get("upload", fileName);
    
        // Lưu file vào thư mục "upload"
        Files.write(filePath, file.getBytes());
    
        // Trả về đường dẫn công khai của file để hiển thị
        return "/upload/" + fileName;
    }
}
