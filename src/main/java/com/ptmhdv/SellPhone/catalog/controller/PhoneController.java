package com.ptmhdv.SellPhone.catalog.controller;

import com.ptmhdv.SellPhone.catalog.dto.PhonesDTO;
import com.ptmhdv.SellPhone.catalog.service.PhoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/phones")
@CrossOrigin
public class PhoneController {

    @Autowired
    private PhoneService phoneService;

    @GetMapping
    public List<PhonesDTO> getAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String brandId)
    {
        return phoneService.searchByFilter(keyword, brandId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PhonesDTO> getById(@PathVariable String id) {
        PhonesDTO dto = phoneService.getById(id);
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public PhonesDTO create(@RequestBody PhonesDTO dto) {
        return phoneService.save(dto);
    }

    @PutMapping("/{id}")
    public PhonesDTO update(@PathVariable String id, @RequestBody PhonesDTO dto) {
        dto.setId(id);
        return phoneService.save(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        phoneService.delete(id);
        return ResponseEntity.ok().build();
    }
}