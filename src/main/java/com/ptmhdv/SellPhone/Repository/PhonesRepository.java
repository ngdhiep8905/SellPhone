package com.ptmhdv.SellPhone.Repository;

import com.ptmhdv.SellPhone.Entity.Phones;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhonesRepository extends JpaRepository<Phones, Long> {

    // theo brand
    List<Phones> findByBrand_BrandId(Long brandId);

    // theo tên (không phân biệt hoa thường)
    List<Phones> findByPhoneNameContainingIgnoreCase(String keyword);

    // theo brand + keyword
    List<Phones> findByBrand_BrandIdAndPhoneNameContainingIgnoreCase(Long brandId, String keyword);
}
