package com.ptmhdv.SellPhone.catalog.mapper;

import com.ptmhdv.SellPhone.catalog.entity.Brands;
import com.ptmhdv.SellPhone.catalog.dto.BrandsDTO;

public class BrandsMapper {
    public Brands toEntity(BrandsDTO dto) {
        Brands brand = new Brands();
        brand.setBrandName(dto.getBrandName());
        brand.setBrandDescription(dto.getDescription());
        return brand;
    }

    public void updateEntity(Brands brand, BrandsDTO dto) {
        brand.setBrandName(dto.getBrandName());
        brand.setBrandDescription(dto.getDescription());
    }

    public BrandsDTO toDTO(Brands brand) {
        BrandsDTO dto = new BrandsDTO();
        dto.setId(brand.getBrandId());
        dto.setBrandName(brand.getBrandName());
        dto.setDescription(brand.getBrandDescription());
        return dto;
    }
}
