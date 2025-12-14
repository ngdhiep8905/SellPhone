package com.ptmhdv.SellPhone.catalog.mapper;

import com.ptmhdv.SellPhone.catalog.entity.Brands;
import com.ptmhdv.SellPhone.catalog.dto.BrandsDTO;

public class BrandsMapper {
    public static BrandsDTO toDTO(Brands e) {
        BrandsDTO d = new BrandsDTO();
        d.setId(e.getBrandId());
        d.setBrandName(e.getBrandName());
        d.setDescription(e.getBrandDescription());
        return d;
    }

    public static Brands toEntity(BrandsDTO d) {
        Brands e = new Brands();
        e.setBrandId(d.getId());
        e.setBrandName(d.getBrandName());
        e.setBrandDescription(d.getDescription());
        return e;
    }
}
