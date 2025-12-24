package com.ptmhdv.SellPhone.catalog.mapper;

import com.ptmhdv.SellPhone.catalog.dto.PhonesDTO;
import com.ptmhdv.SellPhone.catalog.entity.Brands;
import com.ptmhdv.SellPhone.catalog.entity.Phones;
import com.ptmhdv.SellPhone.catalog.entity.ProductImage;

import java.util.List;
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

        // Specs
        d.setChipset(e.getChipset());
        d.setRamSize(e.getRamSize());
        d.setStorageSize(e.getStorageSize());
        d.setScreenInfo(e.getScreenInfo());
        d.setBatteryInfo(e.getBatteryInfo());
        d.setRearCamera(e.getRearCamera());
        d.setFrontCamera(e.getFrontCamera());
        d.setOsVersion(e.getOsVersion());
        d.setColor(e.getColor());

        // Inventory
        d.setStockQuantity(e.getStockQuantity());
        d.setStatus(e.getStatus());

        // Brand
        if (e.getBrand() != null) {
            d.setBrandId(e.getBrand().getBrandId());
            d.setBrandName(e.getBrand().getBrandName());
        }

        // Detail images
        if (e.getProductImages() != null) {
            d.setDetailImages(
                    e.getProductImages() == null
                            ? List.of()
                            : e.getProductImages().stream()
                            .sorted(java.util.Comparator.comparing(ProductImage::getPosition))
                            .map(ProductImage::getImageUrl)
                            .collect(Collectors.toList())
            );

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

        // Specs
        e.setChipset(d.getChipset());
        e.setRamSize(d.getRamSize());
        e.setStorageSize(d.getStorageSize());
        e.setScreenInfo(d.getScreenInfo());
        e.setBatteryInfo(d.getBatteryInfo());
        e.setRearCamera(d.getRearCamera());
        e.setFrontCamera(d.getFrontCamera());
        e.setOsVersion(d.getOsVersion());
        e.setColor(d.getColor());

        // Inventory
        e.setStockQuantity(d.getStockQuantity());
        e.setStatus(d.getStatus());

        e.setBrand(brand);
        return e;
    }

    public static void updateEntity(Phones entity, PhonesDTO dto, Brands brand) {
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
