package com.ptmhdv.sellphone.catalog.repository;

import com.ptmhdv.sellphone.catalog.entity.Brands;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandsRepository extends JpaRepository<Brands, String> {

    boolean existsByBrandName(String brandName);
}
