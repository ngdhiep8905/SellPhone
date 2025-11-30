package com.ptmhdv.SellPhone.Service;

import com.ptmhdv.SellPhone.Entity.Brands;
import com.ptmhdv.SellPhone.Repository.BrandsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class BrandsServiceImpl implements BrandsService {
    private final BrandsRepository brandsRepository;

    @Override
    public List<Brands> getAll() {
        return brandsRepository.findAll();
    }

    @Override
    public Brands getById(Long id) {
        return brandsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Brand not found"));
    }

    @Override
    public Brands create(Brands brand) {
        if (brandsRepository.existsByBrandName(brand.getBrandName())) {
            throw new RuntimeException("Brand name already exists");
        }
        return brandsRepository.save(brand);
    }

    @Override
    public Brands update(Long id, Brands updated) {
        Brands exist = getById(id);
        exist.setBrandName(updated.getBrandName());
        exist.setBrandDescription(updated.getBrandDescription());
        return brandsRepository.save(exist);
    }

    @Override
    public void delete(Long id) {
        brandsRepository.deleteById(id);
    }
}
