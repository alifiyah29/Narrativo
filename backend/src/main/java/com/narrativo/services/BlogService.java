package com.narrativo.services;

import com.narrativo.models.Blog;
import com.narrativo.models.Blog.Visibility;
import java.util.List;
import java.util.Optional;

public interface BlogService {
    // Interface methods don't have bodies - they just declare what methods must be implemented
    Blog createBlog(Blog blog, String username);
    List<Blog> getAllBlogs();
    List<Blog> getBlogsByAuthor(Long authorId);
    List<Blog> getBlogsByVisibility(Visibility visibility);
    Optional<Blog> getBlogById(Long id);
    Blog updateBlog(Blog blog);
    void deleteBlog(Long id);
}