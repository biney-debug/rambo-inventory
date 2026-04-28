package com.rambo.controller;

import com.rambo.dto.DashboardDTO;
import com.rambo.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    // GET /api/dashboard → full business summary
    @GetMapping
    public ResponseEntity<DashboardDTO> summary() {
        return ResponseEntity.ok(dashboardService.getSummary());
    }
}
