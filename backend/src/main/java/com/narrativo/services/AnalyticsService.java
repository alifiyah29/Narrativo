package com.narrativo.services;

import java.util.List;
import java.util.Map;

public interface AnalyticsService {
    Map<String, Object> getUserAnalytics();
    Map<String, Object> getAdminAnalytics();
    List<Map<String, Object>> getMonthlyBlogTrends();
}