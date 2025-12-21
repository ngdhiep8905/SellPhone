package com.ptmhdv.SellPhone.catalog.repository;

import com.ptmhdv.SellPhone.catalog.entity.Brands;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BrandsRepository extends JpaRepository<Brands, String> {

    boolean existsByBrandName(String brandName);

    @Query(value = """
        SELECT MAX(CAST(SUBSTRING(brand_id, 2) AS UNSIGNED))
        FROM brands
        WHERE brand_id REGEXP '^B[0-9]+$'
        """, nativeQuery = true)
    Integer findMaxBrandNumber();
}
