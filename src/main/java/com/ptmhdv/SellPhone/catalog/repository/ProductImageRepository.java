package com.ptmhdv.SellPhone.catalog.repository;

import com.ptmhdv.SellPhone.catalog.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Integer> {
    long countByImageUrlAndDeletedFalse(String imageUrl);
    List<ProductImage> findByPhone_PhoneIdAndDeletedFalseOrderByPositionAsc(String phoneId);
    List<ProductImage> findByPhone_PhoneIdAndDeletedTrue(String phoneId);
    boolean existsByImageUrlAndDeletedFalse(String imageUrl);
}

