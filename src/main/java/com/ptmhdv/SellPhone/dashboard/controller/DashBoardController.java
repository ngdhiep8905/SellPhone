package com.ptmhdv.sellphone.dashboard.controller;

import com.ptmhdv.sellphone.dashboard.DTO.DashboardDTO;
import com.ptmhdv.sellphone.dashboard.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashBoardController {
    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/summary")
    public ResponseEntity<DashboardDTO> getSummary(){
        return ResponseEntity.ok(dashboardService.getSummary());
    }
}
