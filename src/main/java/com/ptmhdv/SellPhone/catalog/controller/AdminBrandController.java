package com.ptmhdv.SellPhone.catalog.controller;

import com.ptmhdv.SellPhone.catalog.dto.BrandsDTO;
import com.ptmhdv.SellPhone.catalog.entity.Brands;
import com.ptmhdv.SellPhone.catalog.mapper.BrandsMapper;
import com.ptmhdv.SellPhone.catalog.service.BrandService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/brands")
public class AdminBrandController {
    private final BrandService brandService;
    private final BrandsMapper brandMapper;

    public AdminBrandController(BrandService brandService, BrandsMapper brandMapper) {
        this.brandService = brandService;
        this.brandMapper = brandMapper;
    }

    @PostMapping
    public ResponseEntity<BrandsDTO> createBrand(@Valid @RequestBody BrandsDTO request) {
        Brands brand = brandMapper.toEntity(request);
        Brands savedBrand = brandService.createBrand(brand);
        return ResponseEntity.ok(brandMapper.toDTO(savedBrand));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BrandsDTO> updateBrand(
            @PathVariable String id,
            @Valid @RequestBody BrandsDTO request
    ) {
        Brands existingBrand = brandService.getBrandById(id)
                .orElseThrow(() -> new RuntimeException("Brand not found"));

        brandMapper.updateEntity(existingBrand, request);
        Brands updatedBrand = brandService.updateBrand(id, existingBrand);
        return ResponseEntity.ok(brandMapper.toDTO(updatedBrand));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBrand(@PathVariable String id) {
        brandService.deleteBrand(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BrandsDTO> getBrandById(@PathVariable String id) {
        Brands brand = brandService.getBrandById(id)
                .orElseThrow(() -> new RuntimeException("Brand not found"));
        return ResponseEntity.ok(brandMapper.toDTO(brand));
    }

    @GetMapping
    public ResponseEntity<List<BrandsDTO>> getAllBrands() {
        List<BrandsDTO> brands = brandService.getAllBrands()
                .stream()
                .map(brandMapper::toDTO)
                .toList();

        return ResponseEntity.ok(brands);
    }
}
