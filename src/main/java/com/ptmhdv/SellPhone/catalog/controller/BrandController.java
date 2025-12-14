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
    public Brands update(@PathVariable String id, @RequestBody Brands brandDetails) {
        // 1. Gán ID từ URL vào đối tượng (để đảm bảo cập nhật đúng)
        brandDetails.setBrandId(id);
        // 2. Gọi hàm save (hàm này có thể xử lý cả Create và Update)
        return brandService.save(brandDetails);
    }
}

