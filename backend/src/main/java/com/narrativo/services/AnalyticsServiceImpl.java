package com.narrativo.services;

import com.narrativo.models.Blog;
import com.narrativo.models.User;
import com.narrativo.repositories.BlogRepository;
import com.narrativo.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        User user = userRepository.findByUsername(username);
        stats.put("lastLogin", user != null && user.getLastLogin() != null ? user.getLastLogin() : "Never");

        return stats;
    }

    @Override
    public Map<String, Object> getAdminAnalytics() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalUsers", userRepository.count());
        stats.put("totalBlogs", blogRepository.count());
        stats.put("totalViews", blogRepository.sumAllViews());

        List<Blog> recentActivity = blogRepository.findTop5ByOrderByCreatedAtDesc();
        stats.put("recentActivity", recentActivity != null ? recentActivity : List.of());

        return stats;
    }

    @Override
    public List<Map<String, Object>> getMonthlyBlogTrends() {
        return blogRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        blog -> blog.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM"))
                ))
                .entrySet().stream()
                .map(entry -> {
                    Map<String, Object> trend = new HashMap<>();
                    trend.put("month", entry.getKey());
                    trend.put("count", entry.getValue() != null ? entry.getValue().size() : 0);
                    return trend;
                })
                .collect(Collectors.toList());
    }
}