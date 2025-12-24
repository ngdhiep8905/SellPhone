package com.ptmhdv.SellPhone.catalog.service;

import com.ptmhdv.SellPhone.catalog.dto.PhonesDTO;
import com.ptmhdv.SellPhone.catalog.entity.Brands;
import com.ptmhdv.SellPhone.catalog.entity.Phones;
import com.ptmhdv.SellPhone.catalog.entity.ProductImage;
import com.ptmhdv.SellPhone.catalog.mapper.PhonesMapper;
import com.ptmhdv.SellPhone.catalog.repository.BrandsRepository; // Giả định bạn đã có repo này
import com.ptmhdv.SellPhone.catalog.repository.PhonesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PhoneService {

    @Autowired
    private PhonesRepository phoneRepo;

    @Autowired
    private BrandsRepository brandRepo;

    @Autowired
    private FileStorageService fileStorageService;

    public List<PhonesDTO> getAll() {
        return phoneRepo.findAll().stream()
                .map(PhonesMapper::toDTO)
                .collect(Collectors.toList());
    }

    public PhonesDTO getById(String id) {
        return phoneRepo.findById(id)
                .map(PhonesMapper::toDTO)
                .orElse(null);
    }


    @Transactional
    public PhonesDTO save(PhonesDTO dto) {

        // 1) VALIDATE + NORMALIZE detail images (tối đa 6 ảnh)
        List<String> incomingUrls = (dto.getDetailImages() == null)
                ? List.of()
                : dto.getDetailImages().stream()
                .filter(s -> s != null && !s.isBlank())
                .map(String::trim)
                .distinct()
                .toList();

        if (incomingUrls.size() > 6) {
            throw new RuntimeException("Tối đa 6 ảnh chi tiết");
        }

        boolean isCreate = (dto.getPhoneId() == null || dto.getPhoneId().trim().isEmpty());
        if (isCreate) {
            dto.setPhoneId(generateNextPhoneId());
        }

        // 2) LOAD BRAND
        Brands brand = null;
        if (dto.getBrandId() != null && !dto.getBrandId().trim().isEmpty()) {
            brand = brandRepo.findById(dto.getBrandId().trim())
                    .orElseThrow(() -> new RuntimeException("Brand không hợp lệ"));
        }

        // 3) LOAD PHONE (nếu chưa có -> tạo mới)
        Phones phone = phoneRepo.findById(dto.getPhoneId()).orElse(null);
        if (phone == null) {
            phone = new Phones();
            phone.setPhoneId(dto.getPhoneId());
            phone.setProductImages(new java.util.ArrayList<>());
        }
        if (phone.getProductImages() == null) {
            phone.setProductImages(new java.util.ArrayList<>());
        }

        // 4) GIỮ lại thông tin cũ để cleanup file
        String oldCoverUrl = (phone.getCoverImageURL() == null) ? null : phone.getCoverImageURL().trim();

        // Snapshot URL ảnh chi tiết cũ (chỉ những ảnh đang ACTIVE = deleted=false)
        java.util.Set<String> oldActiveDetailUrls = phone.getProductImages().stream()
                .filter(img -> img.getDeleted() == null || !img.getDeleted())
                .map(ProductImage::getImageUrl)
                .filter(u -> u != null && !u.isBlank())
                .map(String::trim)
                .collect(java.util.stream.Collectors.toSet());

        // 5) UPDATE FIELDS
        phone.setPhoneName(dto.getPhoneName());
        phone.setPrice(dto.getPrice());
        phone.setCoverImageURL(dto.getCoverImageURL());
        phone.setPhoneDescription(dto.getPhoneDescription());

        phone.setChipset(dto.getChipset());
        phone.setRamSize(dto.getRamSize());
        phone.setStorageSize(dto.getStorageSize());
        phone.setScreenInfo(dto.getScreenInfo());
        phone.setBatteryInfo(dto.getBatteryInfo());
        phone.setRearCamera(dto.getRearCamera());
        phone.setFrontCamera(dto.getFrontCamera());
        phone.setOsVersion(dto.getOsVersion());
        phone.setColor(dto.getColor());

        phone.setStockQuantity(dto.getStockQuantity() == null ? 0 : dto.getStockQuantity());
        phone.setStatus(dto.getStatus() == null || dto.getStatus().isBlank() ? "ACTIVE" : dto.getStatus());
        phone.setBrand(brand);

        // 6) SOFT-DELETE + UPSERT ảnh chi tiết theo incomingUrls
        // Tạo map existing by URL (bao gồm cả ảnh đã deleted=true để có thể "restore" lại)
        java.util.Map<String, ProductImage> existingByUrl = new java.util.HashMap<>();
        for (ProductImage img : phone.getProductImages()) {
            if (img.getImageUrl() == null) continue;
            String key = img.getImageUrl().trim();
            if (!key.isBlank()) existingByUrl.put(key, img);
        }

        // a) Mark tất cả ảnh hiện có -> deleted=true trước, rồi restore những cái còn trong incoming
        for (ProductImage img : phone.getProductImages()) {
            img.setDeleted(true);
        }

        // b) Restore / create theo thứ tự incoming (set position)
        int pos = 0;
        for (String url : incomingUrls) {
            ProductImage img = existingByUrl.get(url);
            if (img == null) {
                img = new ProductImage();
                img.setPhone(phone);
                img.setImageUrl(url);
                phone.getProductImages().add(img);
            }
            img.setDeleted(false);
            img.setPosition(pos++);
        }

        // 7) SAVE
        Phones saved = phoneRepo.save(phone);

        // 8) CLEANUP FILE ẢNH KHÔNG CÒN DÙNG (chỉ cleanup những URL vừa bị loại)
        // a) cleanup cover nếu bị đổi
        String newCoverUrl = (saved.getCoverImageURL() == null) ? null : saved.getCoverImageURL().trim();
        if (oldCoverUrl != null && !oldCoverUrl.isBlank()
                && (newCoverUrl == null || newCoverUrl.isBlank() || !oldCoverUrl.equals(newCoverUrl))) {
            // Xoá file cũ nếu DB không còn dùng (tránh xoá nhầm file đang dùng ở sản phẩm khác)
            fileStorageService.deleteIfUnused(oldCoverUrl);
        }

        // b) cleanup detail images bị remove
        java.util.Set<String> newActiveDetailUrls = incomingUrls.stream().collect(java.util.stream.Collectors.toSet());
        java.util.Set<String> removedDetailUrls = new java.util.HashSet<>(oldActiveDetailUrls);
        removedDetailUrls.removeAll(newActiveDetailUrls);

        for (String removedUrl : removedDetailUrls) {
            fileStorageService.deleteIfUnused(removedUrl);
        }

        return PhonesMapper.toDTO(saved);
    }







    public void delete(String id) {
        phoneRepo.deleteById(id);
    }

    public List<PhonesDTO> searchByFilter(String keyword, String brandId) {
        String finalKeyword = (keyword == null || keyword.trim().isEmpty()) ? null : keyword.trim();
        String finalBrandId = (brandId == null || brandId.trim().isEmpty()) ? null : brandId.trim();

        List<Phones> results = phoneRepo.searchByFilter(finalKeyword, finalBrandId);

        return results.stream()
                .map(PhonesMapper::toDTO)
                .collect(Collectors.toList());
    }

    // Thêm logic trừ kho để dùng trong OrderService sau này
    @Transactional
    public void reduceStock(String phoneId, int quantity) {
        Phones phone = phoneRepo.findById(phoneId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy điện thoại"));
        if (phone.getStockQuantity() < quantity) {
            throw new RuntimeException("Không đủ hàng trong kho!");
        }
        phone.setStockQuantity(phone.getStockQuantity() - quantity);
        phoneRepo.save(phone);
    }
    private synchronized String generateNextPhoneId() {
        Integer maxNum = phoneRepo.findMaxPhoneNumber();
        int next = (maxNum == null) ? 1 : (maxNum + 1);
        return "P" + String.format("%03d", next); // P001, P002...
    }

}