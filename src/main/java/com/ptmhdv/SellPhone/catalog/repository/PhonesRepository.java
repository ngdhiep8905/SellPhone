package com.ptmhdv.SellPhone.catalog.repository;


import com.ptmhdv.SellPhone.catalog.entity.Phones;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhonesRepository extends JpaRepository<Phones, String> {

    List<Phones> findByPhoneNameContainingIgnoreCaseOrBrandContainingIgnoreCase(String name, String brand);

    List<Phones> findByQuantityGreaterThan(Integer quantity);
}
