package com.ptmhdv.SellPhone.catalog.service;

import com.ptmhdv.SellPhone.catalog.entity.Brands;
import com.ptmhdv.SellPhone.catalog.repository.BrandsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandService {

    @Autowired
    private BrandsRepository brandsRepo;

    public List<Brands> getAll() {
        return brandsRepo.findAll();
    }

    public Brands getById(String id) {
        return brandsRepo.findById(id).orElse(null);
    }

    public Brands save(Brands brand) {
        return brandsRepo.save(brand);
    }

    public void delete(String id) {
        brandsRepo.deleteById(id);
    }
}
