package com.ptmhdv.SellPhone.catalog.mapper;

import com.ptmhdv.SellPhone.catalog.entity.Brands;
import com.ptmhdv.SellPhone.catalog.entity.Phones;
import com.ptmhdv.SellPhone.catalog.entity.ProductImage;
import com.ptmhdv.SellPhone.catalog.dto.PhonesDTO;
import java.util.stream.Collectors;

public class PhonesMapper {

    public static PhonesDTO toDTO(Phones e) {
        if (e == null) return null;

        PhonesDTO d = new PhonesDTO();
        d.setPhoneId(e.getPhoneId());
        d.setPhoneName(e.getPhoneName());
        d.setPrice(e.getPrice());
        d.setCoverImageURL(e.getCoverImageURL());
        d.setPhoneDescription(e.getPhoneDescription());

        // Map thông số kỹ thuật
        d.setChipset(e.getChipset());
        d.setRamSize(e.getRamSize());
        d.setStorageSize(e.getStorageSize());
        d.setScreenInfo(e.getScreenInfo());
        d.setBatteryInfo(e.getBatteryInfo());
        d.setRearCamera(e.getRearCamera());
        d.setFrontCamera(e.getFrontCamera());
        d.setOsVersion(e.getOsVersion());
        d.setColor(e.getColor());
        d.setStockQuantity(e.getStockQuantity());
        d.setStatus(e.getStatus());

        // Map Brand info
        if (e.getBrand() != null) {
            d.setBrandId(e.getBrand().getBrandId());
            d.setBrandName(e.getBrand().getBrandName());
        }

        // Map danh sách URL ảnh chi tiết
        if (e.getProductImages() != null) {
            d.setDetailImages(e.getProductImages().stream()
                    .map(ProductImage::getImageUrl)
                    .collect(Collectors.toList()));
        }

        return d;
    }

    public static Phones toEntity(PhonesDTO d, Brands brand) {
        if (d == null) return null;

        Phones e = new Phones();
        e.setPhoneId(d.getPhoneId());
        e.setPhoneName(d.getPhoneName());
        e.setPrice(d.getPrice());
        e.setCoverImageURL(d.getCoverImageURL());
        e.setPhoneDescription(d.getPhoneDescription());

        // Map thông số kỹ thuật
        e.setChipset(d.getChipset());
        e.setRamSize(d.getRamSize());
        e.setStorageSize(d.getStorageSize());
        e.setScreenInfo(d.getScreenInfo());
        e.setBatteryInfo(d.getBatteryInfo());
        e.setRearCamera(d.getRearCamera());
        e.setFrontCamera(d.getFrontCamera());
        e.setOsVersion(d.getOsVersion());
        e.setColor(d.getColor());
        e.setStockQuantity(d.getStockQuantity());
        e.setStatus(d.getStatus());

        e.setBrand(brand);

        return e;
    }

    public static void updateEntity(Phones entity, PhonesDTO dto, Brands brand) {
        // ID: thường KHÔNG nên đổi khi update (đã lấy theo path variable)
        // entity.setPhoneId(dto.getPhoneId()); // thường bỏ dòng này

        entity.setPhoneName(dto.getPhoneName());
        entity.setBrand(brand);

        entity.setChipset(dto.getChipset());
        entity.setRamSize(dto.getRamSize());
        entity.setStorageSize(dto.getStorageSize());
        entity.setScreenInfo(dto.getScreenInfo());
        entity.setBatteryInfo(dto.getBatteryInfo());
        entity.setRearCamera(dto.getRearCamera());
        entity.setFrontCamera(dto.getFrontCamera());
        entity.setOsVersion(dto.getOsVersion());
        entity.setColor(dto.getColor());

        entity.setPrice(dto.getPrice());
        entity.setStockQuantity(dto.getStockQuantity());
        entity.setStatus(dto.getStatus());
        entity.setCoverImageURL(dto.getCoverImageURL());
        entity.setPhoneDescription(dto.getPhoneDescription());
    }
}