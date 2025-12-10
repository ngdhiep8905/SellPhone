package com.ptmhdv.SellPhone.catalog.repository;

import com.ptmhdv.SellPhone.catalog.entity.Brands;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandsRepository extends JpaRepository<Brands, String> {

    boolean existsByBrandName(String brandName);
}
