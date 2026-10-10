package com.tara.analytics.controller;

import com.tara.analytics.dto.AnalyticsDtos;
import com.tara.analytics.service.AnalyticsService;
import com.tara.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/analytics")
@Tag(name = "Analytics & Metrics", description = "Enterprise observability, latency percentiles, token usage, cost tracking, and version trends")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Get aggregated analytics dashboard KPIs and historical timeseries trends")
    public ResponseEntity<ApiResponse<AnalyticsDtos.DashboardAnalyticsResponse>> getDashboardAnalytics(
            @RequestParam(required = false, defaultValue = "14") int days) {
        AnalyticsDtos.DashboardAnalyticsResponse data = analyticsService.getDashboardAnalytics(days);
        return ResponseEntity.ok(ApiResponse.ok(data));
    }
}
