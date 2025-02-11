package com.narrativo.services;

import com.narrativo.repositories.BlogRepository;
import com.narrativo.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final BlogRepository blogRepository;
    private final UserRepository userRepository;

    @Override
    public Map<String, Object> getUserAnalytics() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalBlogs", blogRepository.countByAuthorUsername(username));
        stats.put("totalViews", blogRepository.sumViewsByAuthor(username));
        stats.put("lastLogin", userRepository.findByUsername(username).getLastLogin());
        
        return stats;
    }

    @Override
    public Map<String, Object> getAdminAnalytics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("totalBlogs", blogRepository.count());
        stats.put("totalViews", blogRepository.sumAllViews());
        stats.put("recentActivity", blogRepository.findTop5ByOrderByCreatedAtDesc());
        return stats;
    }
}