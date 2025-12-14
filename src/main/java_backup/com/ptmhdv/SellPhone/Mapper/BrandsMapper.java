package com.ptmhdv.SellPhone.Mapper;

import com.ptmhdv.SellPhone.Entity.Brands;
import com.ptmhdv.SellPhone.dto.BrandsDTO;

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
