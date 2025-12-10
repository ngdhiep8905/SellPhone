package com.ptmhdv.SellPhone.catalog.service;

import com.ptmhdv.SellPhone.catalog.entity.Brands;

import java.util.List;
import java.util.Optional;

public interface BrandService {
    Brands createBrand(Brands brand);

    Brands updateBrand(String id, Brands brand);

    void deleteBrand(String id);

    Optional<Brands> getBrandById(String id);

    List<Brands> getAllBrands();
}
