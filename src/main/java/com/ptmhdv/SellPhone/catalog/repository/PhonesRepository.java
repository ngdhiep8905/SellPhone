package com.ptmhdv.sellphone.catalog.repository;

import com.ptmhdv.sellphone.catalog.entity.Phones;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhonesRepository extends JpaRepository<Phones, String> {

    List<Phones> findByBrand_BrandId(String brandId);

    List<Phones> findByPhoneNameContainingIgnoreCase(String keyword);
}
