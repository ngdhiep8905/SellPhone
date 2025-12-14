package com.ptmhdv.SellPhone.catalog.repository;

import com.ptmhdv.SellPhone.catalog.entity.Phones;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhonesRepository extends JpaRepository<Phones, String> {

    List<Phones> findByBrand_BrandId(String brandId);

    List<Phones> findByPhoneNameContainingIgnoreCase(String keyword);
}
