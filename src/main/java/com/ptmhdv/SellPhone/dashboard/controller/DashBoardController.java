package com.ptmhdv.SellPhone.dashboard.controller;

import com.ptmhdv.SellPhone.dashboard.DTO.DashboardDTO;
import com.ptmhdv.SellPhone.dashboard.service.DashboardService;
import com.ptmhdv.SellPhone.order.entity.Orders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashBoardController {

    @Autowired
    private DashboardService dashboardService;

    // ============================
    // 1) SUMMARY
    // ============================
    @GetMapping("/summary")
    public ResponseEntity<DashboardDTO> getSummary() {
        return ResponseEntity.ok(dashboardService.getSummary());
    }
    // ============================
    // 2) DOANH THU THEO THÁNG (overview chart)
    // GET /api/dashboard/revenue-monthly
    // ============================
    @GetMapping("/revenue-monthly")
    public ResponseEntity<List<Map<String, Object>>> getRevenueMonthly() {

        List<Object[]> data = dashboardService.getRevenueMonthly();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Object[] row : data) {
            Map<String, Object> map = new HashMap<>();
            map.put("month", row[0]);
            map.put("total", row[1]);
            result.add(map);
        }

        return ResponseEntity.ok(result);
    }

    // ============================
    // 3) FILTER REVENUE (advanced chart)
    // GET /api/dashboard/revenue?month=...&year=...
    // ============================
    @GetMapping("/revenue")
    public ResponseEntity<List<Map<String, Object>>> getRevenue(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String brandId
    ) {
        List<Map<String, Object>> result = dashboardService.filterRevenue(month, year, brandId);
        return ResponseEntity.ok(result);
    }


    // ============================
    // 4) TOP 5 SẢN PHẨM BÁN CHẠY
    // GET /api/dashboard/top-products
    // ============================
    @GetMapping("/top-products")
    public ResponseEntity<List<Map<String, Object>>> getTopProducts() {

        List<Object[]> data = dashboardService.getTopProducts();

        List<Map<String, Object>> result = new ArrayList<>();

        for (Object[] row : data) {
            Map<String, Object> map = new HashMap<>();
            map.put("phoneName", row[0]);
            map.put("totalSold", row[1]);
            result.add(map);
        }

        return ResponseEntity.ok(result);
    }


    // ============================
    // 5) DOANH THU THEO THƯƠNG HIỆU
    // GET /api/dashboard/brand-sales
    // ============================
    @GetMapping("/brand-sales")
    public ResponseEntity<List<Map<String, Object>>> getRevenueByBrand() {

        List<Object[]> data = dashboardService.getRevenueByBrand();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Object[] row : data) {
            Map<String, Object> map = new HashMap<>();
            map.put("brandName", row[0]);
            map.put("total", row[1]);
            result.add(map);
        }

        return ResponseEntity.ok(result);
    }

}
