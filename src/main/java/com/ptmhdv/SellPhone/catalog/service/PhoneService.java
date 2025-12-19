package com.ptmhdv.SellPhone.catalog.service;

import com.ptmhdv.SellPhone.catalog.dto.PhonesDTO;
import com.ptmhdv.SellPhone.catalog.entity.Brands;
import com.ptmhdv.SellPhone.catalog.entity.Phones;
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
        Phones phone = phoneRepo.findById(id).orElse(null);
        return PhonesMapper.toDTO(phone);
    }

    @Transactional
    public PhonesDTO save(PhonesDTO dto) {
        // Tìm brand từ brandId trong DTO
        Brands brand = null;
        if (dto.getBrandId() != null) {
            brand = brandRepo.findById(dto.getBrandId()).orElse(null);
        }

        // Chuyển DTO thành Entity
        Phones phone = PhonesMapper.toEntity(dto, brand);

        // Lưu xuống DB
        Phones savedPhone = phoneRepo.save(phone);

        // Trả về DTO sau khi lưu
        return PhonesMapper.toDTO(savedPhone);
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
}