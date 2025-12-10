package com.ptmhdv.SellPhone.catalog.controller;

import com.ptmhdv.SellPhone.catalog.entity.Brands;
import com.ptmhdv.SellPhone.catalog.entity.Phones;
import com.ptmhdv.SellPhone.catalog.service.BrandService;
import com.ptmhdv.SellPhone.catalog.service.PhoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private PhoneService phoneService;
    @Autowired
    private BrandService brandService;
    @GetMapping("/phones")
    public ResponseEntity<List<Phones>> getAllPhones(
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortOrder,
            @RequestParam(required = false) String category) {
        try {
            List<Phones> phones;
                if (sortBy != null && !sortBy.isEmpty()) {
                    phones = phoneService.getAllPhonesSorted(sortBy, sortOrder);
                } else {
                    phones = phoneService.getAllPhones();
                }

            // Add cache control headers for better performance
            return ResponseEntity.ok()
                    .cacheControl(org.springframework.http.CacheControl.maxAge(60, java.util.concurrent.TimeUnit.SECONDS))
                    .body(phones);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get book by ID
     */
    @GetMapping("/phones/{id}")
    public ResponseEntity<Phones> getBrandById(@PathVariable String id) {
        try {
            Phones phone = phoneService.getPhoneById(id);
            if (phone != null) {
                return ResponseEntity.ok(phone);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Search books
     */
    @GetMapping("/phones/search")
    public ResponseEntity<List<Phones>> searchBooks(@RequestParam String query) {
        try {
            List<Phones> books = phoneService.searchPhones(query);
            return ResponseEntity.ok(books);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    @GetMapping("/brands")
    public ResponseEntity<List<Brands>> getAllBrands(
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortOrder,
            @RequestParam(required = false) String category) {
        try {
            List<Brands> brands;
                brands = brandService.getAllBrands();

            // Add cache control headers for better performance
            return ResponseEntity.ok()
                    .cacheControl(org.springframework.http.CacheControl.maxAge(60, java.util.concurrent.TimeUnit.SECONDS))
                    .body(brands);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get book by ID
     */
    @GetMapping("/brands/{id}")
    public ResponseEntity<Brands> getBookById(@PathVariable String id) {
        try {
            Brands brands = brandService.getBrandById(id)
                    .orElseThrow(() -> new RuntimeException("Brand not found"));;
            if (brands != null) {
                return ResponseEntity.ok(brands);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
