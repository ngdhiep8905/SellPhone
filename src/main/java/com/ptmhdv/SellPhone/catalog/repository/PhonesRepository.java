package com.ptmhdv.SellPhone.catalog.repository;

import com.ptmhdv.SellPhone.catalog.entity.Phones;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PhonesRepository extends JpaRepository<Phones, String> {

    List<Phones> findByBrand_BrandId(String brandId);

    List<Phones> findByPhoneNameContainingIgnoreCase(String keyword);

    @Query("""
        SELECT p FROM Phones p
        WHERE
          (:keyword IS NULL OR
            LOWER(p.phoneName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(COALESCE(p.chipset, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(COALESCE(p.phoneDescription, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
          )
          AND (:brandId IS NULL OR p.brand.brandId = :brandId)
          AND p.status = 'ACTIVE'
        """)
    List<Phones> searchByFilter(@Param("keyword") String keyword, @Param("brandId") String brandId);
    // Lấy số lớn nhất từ phone_id dạng P001, P002...
    @Query(value = """
        SELECT MAX(CAST(SUBSTRING(phone_id, 2) AS UNSIGNED))
        FROM phones
        WHERE phone_id REGEXP '^P[0-9]+$'
        """, nativeQuery = true)
    Integer findMaxPhoneNumber();
}

