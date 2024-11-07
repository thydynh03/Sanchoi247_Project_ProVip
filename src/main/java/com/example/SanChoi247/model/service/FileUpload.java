package com.example.SanChoi247.model.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface FileUpload {
    String uploadFile(MultipartFile multipartFile) throws IOException;
}
