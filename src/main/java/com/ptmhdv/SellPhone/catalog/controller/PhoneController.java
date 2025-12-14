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

    @GetMapping
    public List<Phones> getAll() {
        return phoneService.getAll();
    }

    @GetMapping("/{id}")
    public Phones getById(@PathVariable String id) {
        return phoneService.getById(id);
    }

    @GetMapping("/search")
    public List<Phones> search(@RequestParam String keyword) {
        return phoneService.search(keyword);
    }

    @PostMapping
    public Phones save(@RequestBody Phones phone) {
        return phoneService.save(phone);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        phoneService.delete(id);
    }
}

