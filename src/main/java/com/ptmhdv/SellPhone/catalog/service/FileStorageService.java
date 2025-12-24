package com.ptmhdv.SellPhone.catalog.service;

import com.ptmhdv.SellPhone.catalog.repository.PhonesRepository;
import com.ptmhdv.SellPhone.catalog.repository.ProductImageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileStorageService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    private final PhonesRepository phonesRepository;
    private final ProductImageRepository productImageRepository;

    public FileStorageService(PhonesRepository phonesRepository,
                              ProductImageRepository productImageRepository) {
        this.phonesRepository = phonesRepository;
        this.productImageRepository = productImageRepository;
    }

    /**
     * Xóa file theo URL nếu:
     * - không còn dùng làm cover ở bảng phones
     * - và không còn ảnh active (deleted=false) ở bảng product_images
     */
    public void deleteIfUnused(String url) {
        if (url == null || url.isBlank()) return;
        String normalized = url.trim();

        // còn dùng làm cover -> không xóa
        if (phonesRepository.existsByCoverImageURL(normalized)) return;

        // còn dùng làm ảnh active -> không xóa
        if (productImageRepository.existsByImageUrlAndDeletedFalse(normalized)) return;

        // không còn reference active -> xóa file vật lý
        deleteByUrl(normalized);
    }

    public void deleteByUrl(String url) {
        if (url == null || url.isBlank()) return;

        String filename = Paths.get(url).getFileName().toString();
        Path path = Paths.get(uploadDir, filename);

        try {
            Files.deleteIfExists(path);
        } catch (Exception e) {
            // Không throw để tránh rollback DB
            System.err.println("Không xoá được file: " + path);
        }
    }
}
