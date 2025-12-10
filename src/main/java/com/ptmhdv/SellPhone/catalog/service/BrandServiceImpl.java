package com.ptmhdv.SellPhone.catalog.service;


import com.ptmhdv.SellPhone.catalog.entity.Brands;
import com.ptmhdv.SellPhone.catalog.repository.BrandsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BrandServiceImpl implements BrandService {
    private final BrandsRepository brandRepository;

    public BrandServiceImpl(BrandsRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    @Override
    public Brands createBrand(Brands brand) {
        if (brandRepository.existsByBrandName(brand.getBrandName())) {
            throw new RuntimeException("Brand name already exists");
        }
        return brandRepository.save(brand);
    }

    @Override
    public Brands updateBrand(String id, Brands brand) {
        Brands existingBrand = brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Brand not found"));

        existingBrand.setBrandName(brand.getBrandName());
        existingBrand.setBrandDescription(brand.getBrandDescription());

        return brandRepository.save(existingBrand);
    }

    @Override
    public void deleteBrand(String id) {
        if (!brandRepository.existsById(id)) {
            throw new RuntimeException("Brand not found");
        }
        brandRepository.deleteById(id);
    }

    @Override
    public Optional<Brands> getBrandById(String id) {
        return brandRepository.findById(id);
    }

    @Override
    public List<Brands> getAllBrands() {
        return brandRepository.findAll();
    }
}
