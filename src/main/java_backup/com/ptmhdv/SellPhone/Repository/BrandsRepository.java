package com.ptmhdv.SellPhone.Repository;

import com.ptmhdv.SellPhone.Entity.Brands;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandsRepository extends JpaRepository<Brands, String> {

    boolean existsByBrandName(String brandName);
}
