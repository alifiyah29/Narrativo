package com.narrativo.services;

import java.util.Map;

public interface AnalyticsService {
    Map<String, Object> getUserAnalytics();
    Map<String, Object> getAdminAnalytics();
}