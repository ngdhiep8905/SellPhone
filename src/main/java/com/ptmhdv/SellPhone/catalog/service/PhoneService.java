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

        boolean isCreate = (dto.getPhoneId() == null || dto.getPhoneId().trim().isEmpty());
        if (isCreate) {
            dto.setPhoneId(generateNextPhoneId());
        }

        Brands brand = null;
        if (dto.getBrandId() != null && !dto.getBrandId().trim().isEmpty()) {
            brand = brandRepo.findById(dto.getBrandId())
                    .orElseThrow(() -> new RuntimeException("Brand không hợp lệ"));
        }

        Phones phone = phoneRepo.findById(dto.getPhoneId()).orElse(null);
        if (phone == null) {
            phone = new Phones();
            phone.setPhoneId(dto.getPhoneId());
            phone.setProductImages(new java.util.ArrayList<>());
        }

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

        // ✅ replace toàn bộ ảnh chi tiết theo dto.detailImages
        if (dto.getDetailImages() != null) {
            if (phone.getProductImages() == null) {
                phone.setProductImages(new java.util.ArrayList<>());
            } else {
                phone.getProductImages().clear(); // orphanRemoval => xoá DB ảnh cũ
            }

            for (String url : dto.getDetailImages()) {
                if (url == null || url.isBlank()) continue;

                ProductImage img = new ProductImage();
                img.setImageUrl(url.trim());
                img.setPhone(phone);
                phone.getProductImages().add(img);
            }
        }

        Phones saved = phoneRepo.save(phone);
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