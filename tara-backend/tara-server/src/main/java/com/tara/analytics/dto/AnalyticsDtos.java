package com.tara.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

public class AnalyticsDtos {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KpiMetrics {
        private long totalExecutions;
        private double successRate;
        private int avgLatencyMs;
        private int p95LatencyMs;
        private long totalTokens;
        private BigDecimal totalCostUsd;
        private double avgEvalScore;
        private double securityHealthScore;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrendPoint {
        private String timestamp;
        private long requests;
        private int latencyMs;
        private double costUsd;
        private long tokenCount;
        private double evalScore;
        private int securityViolations;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VersionComparisonPoint {
        private String version;
        private String promptName;
        private double successRate;
        private double avgScore;
        private int p95LatencyMs;
        private double totalCost;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DashboardAnalyticsResponse {
        private KpiMetrics kpis;
        private List<TrendPoint> timeseriesTrends;
        private List<VersionComparisonPoint> versionComparisons;
    }
}
