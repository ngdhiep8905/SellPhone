package com.ptmhdv.SellPhone.catalog.service;

import com.ptmhdv.SellPhone.catalog.entity.Brands;
import com.ptmhdv.SellPhone.catalog.repository.BrandsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BrandService {

    @Autowired
    private BrandsRepository brandsRepo;

    public List<Brands> getAll() {
        return brandsRepo.findAll();
    }

    public Brands getById(String id) {
        return brandsRepo.findById(id).orElse(null);
    }

    @Transactional
    public Brands save(Brands brand) {
        // Trim cơ bản
        if (brand.getBrandName() != null) brand.setBrandName(brand.getBrandName().trim());
        if (brand.getBrandDescription() != null) brand.setBrandDescription(brand.getBrandDescription().trim());

        // Tạo mới -> tự sinh brandId
        if (brand.getBrandId() == null || brand.getBrandId().isBlank()) {
            brand.setBrandId(generateNextBrandId());
        }

        return brandsRepo.save(brand);
    }

    private String generateNextBrandId() {
        Integer max = brandsRepo.findMaxBrandNumber(); // ví dụ trả 3 nếu max là B003
        int next = (max == null ? 1 : max + 1);

        if (next > 999) {
            throw new IllegalStateException("Brand ID overflow: exceeded B999");
        }

        return String.format("B%03d", next); // B001, B002, ...
    }

    public void delete(String id) {
        brandsRepo.deleteById(id);
    }
}

