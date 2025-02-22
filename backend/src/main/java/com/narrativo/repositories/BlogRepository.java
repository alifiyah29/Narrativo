package com.narrativo.repositories;

import com.narrativo.models.Blog;
import com.narrativo.models.Blog.Visibility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface BlogRepository extends JpaRepository<Blog, Long> {

    List<Blog> findByUserId(Long userId); 

    List<Blog> findByVisibility(Visibility visibility);

    @Query("SELECT COUNT(b) FROM Blog b WHERE b.user.username = :username")
    Long countByUserUsername(String username); 

    @Query("SELECT SUM(b.views) FROM Blog b WHERE b.user.username = :username")
    Long sumViewsByUser(String username); 

    @Query("SELECT SUM(b.views) FROM Blog b")
    Long sumAllViews();

    List<Blog> findTop5ByOrderByCreatedAtDesc();
}