package com.narrativo.repositories;

import com.narrativo.models.Blog;
import com.narrativo.models.Blog.Visibility;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BlogRepository extends JpaRepository<Blog, Long> {
    List<Blog> findByAuthorId(Long authorId); 
    List<Blog> findByVisibility(Visibility visibility);
}