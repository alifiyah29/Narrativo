package com.narrativo.services;

import com.narrativo.models.Blog;
import java.util.List;
import java.util.Optional;

public interface BlogService {
    Blog createBlog(Blog blog, String username);
    List<Blog> getAllBlogs();
    List<Blog> getBlogsByAuthor(Long authorId);
    List<Blog> getBlogsByVisibility(Blog.Visibility visibility, String username);
    Optional<Blog> getBlogById(Long id);
    Blog updateBlog(Blog blog);
    void deleteBlog(Long id);
    Blog incrementViews(Long id);
}