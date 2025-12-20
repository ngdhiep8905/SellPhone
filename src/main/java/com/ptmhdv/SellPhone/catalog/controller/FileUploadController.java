package com.ptmhdv.SellPhone.catalog.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "*")
public class FileUploadController {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Value("${app.upload.urlPrefix:/images/}")
    private String urlPrefix;

    @PostMapping("/upload")
    public Map<String, String> upload(@RequestParam("file") MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File rỗng");
        }

        // Lấy extension an toàn
        String original = StringUtils.cleanPath(file.getOriginalFilename() == null ? "" : file.getOriginalFilename());
        String ext = "";
        int dot = original.lastIndexOf('.');
        if (dot >= 0) ext = original.substring(dot).toLowerCase();

        // (tùy chọn) chặn file không phải ảnh
        if (!ext.matches("\\.(png|jpg|jpeg|webp|gif)$")) {
            throw new RuntimeException("Chỉ cho phép ảnh: png/jpg/jpeg/webp/gif");
        }

        String filename = UUID.randomUUID() + ext;

        Path dir = Paths.get(uploadDir);
        Files.createDirectories(dir);

        Path target = dir.resolve(filename);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        // URL để frontend dùng <img src="...">
        String url = urlPrefix + filename; // /images/uuid.jpg
        return Map.of("url", url);
    }
}
