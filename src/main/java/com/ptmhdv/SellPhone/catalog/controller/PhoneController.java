package com.ptmhdv.SellPhone.catalog.controller;

import com.ptmhdv.SellPhone.catalog.entity.Phones;
import com.ptmhdv.SellPhone.catalog.service.PhoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/phones")
@CrossOrigin
public class PhoneController {

    @Autowired
    private PhoneService phoneService;

    @GetMapping("/{id}")
    public Phones getById(@PathVariable String id) {
        return phoneService.getById(id);
    }

    @PostMapping
    public Phones save(@RequestBody Phones phone) {
        return phoneService.save(phone);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        phoneService.delete(id);
    }
    @GetMapping
    public List<Phones> getAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String brandId)
    {
        if ( (keyword == null || keyword.isEmpty()) && (brandId == null || brandId.isEmpty()) ) {
            return phoneService.getAll();
        }

        // Gọi Service lọc tổng hợp
        return phoneService.searchByFilter(keyword, brandId);
    }
    @PutMapping("/{id}")
    public Phones update(@PathVariable String id, @RequestBody Phones phoneDetails) {
        phoneDetails.setPhoneId(id);
        return phoneService.save(phoneDetails);
    }
}

