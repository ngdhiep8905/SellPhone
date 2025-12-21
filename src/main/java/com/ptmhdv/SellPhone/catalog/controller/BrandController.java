package com.ptmhdv.SellPhone.catalog.controller;

import com.ptmhdv.SellPhone.catalog.entity.Brands;
import com.ptmhdv.SellPhone.catalog.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brands")
@CrossOrigin
public class BrandController {

    @Autowired
    private BrandService brandService;

    @GetMapping
    public List<Brands> getAll() {
        return brandService.getAll();
    }

    @GetMapping("/{id}")
    public Brands getById(@PathVariable String id) {
        return brandService.getById(id);
    }

    @PostMapping
    public Brands save(@RequestBody Brands brand) {
        return brandService.save(brand);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        brandService.delete(id);
    }
    @PutMapping("/{id}")
    public Brands update(@PathVariable String id, @RequestBody Brands input) {
        Brands existing = brandService.getById(id);
        if (existing == null) return null; // hoặc throw 404

        if (input.getBrandName() != null) existing.setBrandName(input.getBrandName());
        if (input.getBrandDescription() != null) existing.setBrandDescription(input.getBrandDescription());

        return brandService.save(existing);
    }


}

