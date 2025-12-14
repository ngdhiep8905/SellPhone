package com.ptmhdv.SellPhone.catalog.service;

import com.ptmhdv.SellPhone.catalog.entity.Phones;
import com.ptmhdv.SellPhone.catalog.repository.PhonesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PhoneService {

    @Autowired
    private PhonesRepository phoneRepo;

    public List<Phones> getAll() {
        return phoneRepo.findAll();
    }

    public Phones getById(String id) {
        return phoneRepo.findById(id).orElse(null);
    }

    public Phones save(Phones phone) {
        return phoneRepo.save(phone);
    }

    public void delete(String id) {
        phoneRepo.deleteById(id);
    }

    public List<Phones> search(String keyword) {
        return phoneRepo.findByPhoneNameContainingIgnoreCase(keyword);
    }
    public List<Phones> searchByFilter(String keyword, String brandId) {
        String finalKeyword = (keyword == null || keyword.trim().isEmpty()) ? null : keyword.trim();
        String finalBrandId = (brandId == null || brandId.trim().isEmpty()) ? null : brandId.trim();

        // Nếu cả hai đều null, trả về tất cả
        if (finalKeyword == null && finalBrandId == null) {
            return phoneRepo.findAll();
        }

        return phoneRepo.searchByFilter(finalKeyword, finalBrandId);
    }
}

