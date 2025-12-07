package com.ptmhdv.sellphone.catalog.mapper;

import com.ptmhdv.sellphone.catalog.entity.Brands;
import com.ptmhdv.sellphone.catalog.entity.Phones;
import com.ptmhdv.sellphone.catalog.dto.PhonesDTO;

public class PhonesMapper {
    public static PhonesDTO toDTO(Phones e) {
        PhonesDTO d = new PhonesDTO();
        d.setId(e.getPhoneId());
        d.setPhoneName(e.getPhoneName());
        d.setPrice(e.getPrice());
        d.setCoverImageURL(e.getCoverImageURL());
        d.setPhoneDescription(e.getPhoneDescription());
        if (e.getBrand() != null)
            d.setBrandId(e.getBrand().getBrandId());

        return d;
    }

    public static Phones toEntity(PhonesDTO d, Brands brand) {
        Phones e = new Phones();
        e.setPhoneId(d.getId());
        e.setPhoneName(d.getPhoneName());
        e.setPrice(d.getPrice());
        e.setCoverImageURL(d.getCoverImageURL());
        e.setPhoneDescription(d.getPhoneDescription());
        e.setBrand(brand);

        return e;
    }
}
