package com.narrativo.controllers;

import com.narrativo.services.AnalyticsService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/user")
    public Map<String, Object> getUserAnalytics() {
        return analyticsService.getUserAnalytics();
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> getAdminAnalytics() {
        return analyticsService.getAdminAnalytics();
    }

    @GetMapping("/monthly-trends")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> getMonthlyBlogTrends() {
        List<Map<String, Object>> trends = analyticsService.getMonthlyBlogTrends();
        return ResponseEntity.ok(trends);
    }
}