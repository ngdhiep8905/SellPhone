package com.ptmhdv.SellPhone.catalog.controller;

import com.ptmhdv.SellPhone.catalog.dto.PhonesDTO;
import com.ptmhdv.SellPhone.catalog.service.PhoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/phones")
@CrossOrigin(origins = "*") // dev: cho phép tất cả; nếu production thì khóa lại theo domain
public class PhoneController {

    @Autowired
    private PhoneService phoneService;

    @GetMapping
    public ResponseEntity<List<PhonesDTO>> getAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String brandId) {

        // Normalize: "" -> null, trim keyword
        keyword = (keyword == null || keyword.trim().isEmpty()) ? null : keyword.trim();
        brandId = (brandId == null || brandId.trim().isEmpty()) ? null : brandId.trim();

        List<PhonesDTO> results = phoneService.searchByFilter(keyword, brandId);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PhonesDTO> getById(@PathVariable String id) {
        PhonesDTO dto = phoneService.getById(id);
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<PhonesDTO> create(@RequestBody PhonesDTO dto) {
        PhonesDTO saved = phoneService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PhonesDTO> update(@PathVariable String id, @RequestBody PhonesDTO dto) {
        dto.setPhoneId(id);   // FIX CHÍNH
        PhonesDTO saved = phoneService.save(dto);
        return ResponseEntity.ok(saved);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        phoneService.delete(id);
        return ResponseEntity.ok().build();
    }
}
