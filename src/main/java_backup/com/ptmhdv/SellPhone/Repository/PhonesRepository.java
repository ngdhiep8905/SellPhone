package com.ptmhdv.SellPhone.Repository;

import com.ptmhdv.SellPhone.Entity.Phones;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhonesRepository extends JpaRepository<Phones, String> {

    List<Phones> findByBrand_BrandId(String brandId);

    List<Phones> findByPhoneNameContainingIgnoreCase(String keyword);
}
