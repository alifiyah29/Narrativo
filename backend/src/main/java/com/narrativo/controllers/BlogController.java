package com.narrativo.controllers;

import com.narrativo.models.Blog;
import com.narrativo.services.BlogService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/blogs")
@RequiredArgsConstructor
public class BlogController {

    private final BlogService blogService;

    @PostMapping
    public ResponseEntity<Blog> createBlog(@AuthenticationPrincipal UserDetails userDetails, @RequestBody Blog blog) {
        Blog savedBlog = blogService.createBlog(blog, userDetails.getUsername());
        return ResponseEntity.ok(savedBlog);
    }

    @GetMapping
    public ResponseEntity<List<Blog>> getAllBlogs() {
        return ResponseEntity.ok(blogService.getAllBlogs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Blog> getBlogById(@PathVariable Long id) {
        Optional<Blog> blog = blogService.getBlogById(id);
        return blog.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{authorId}")
    public ResponseEntity<List<Blog>> getBlogsByAuthor(@PathVariable Long authorId) {
        return ResponseEntity.ok(blogService.getBlogsByAuthor(authorId));
    }

    @GetMapping("/visibility/{visibility}")
    public ResponseEntity<List<Blog>> getBlogsByVisibility(@PathVariable Blog.Visibility visibility) {
        return ResponseEntity.ok(blogService.getBlogsByVisibility(visibility));
    }

@PutMapping("/{id}")
    public ResponseEntity<?> updateBlog(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestBody Blog updatedBlog) {
        
        try {
            // First, check if the blog exists
            Optional<Blog> existingBlog = blogService.getBlogById(id);
            
            if (existingBlog.isEmpty()) {
                return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new HashMap<String, String>() {{
                        put("message", "Blog not found with id: " + id);
                    }});
            }
            
            // Check if the current user is the author
            Blog blog = existingBlog.get();
            if (!blog.getAuthor().getUsername().equals(userDetails.getUsername())) {
                return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new HashMap<String, String>() {{
                        put("message", "You don't have permission to update this blog");
                    }});
            }
            
            // Update only the allowed fields
            blog.setTitle(updatedBlog.getTitle());
            blog.setContent(updatedBlog.getContent());
            blog.setVisibility(updatedBlog.getVisibility());
            blog.setUpdatedAt(LocalDateTime.now());
            
            // Save the updated blog
            Blog saved = blogService.updateBlog(blog);
            return ResponseEntity.ok(saved);
            
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new HashMap<String, String>() {{
                    put("message", "Error updating blog: " + e.getMessage());
                }});
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBlog(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        
        try {
            // Check if the blog exists
            Optional<Blog> existingBlog = blogService.getBlogById(id);
            
            if (existingBlog.isEmpty()) {
                return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new HashMap<String, String>() {{
                        put("message", "Blog not found with id: " + id);
                    }});
            }
            
            // Check if the current user is the author
            Blog blog = existingBlog.get();
            if (!blog.getAuthor().getUsername().equals(userDetails.getUsername())) {
                return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new HashMap<String, String>() {{
                        put("message", "You don't have permission to delete this blog");
                    }});
            }
            
            // Delete the blog
            blogService.deleteBlog(id);
            return ResponseEntity
                .ok()
                .body(new HashMap<String, String>() {{
                    put("message", "Blog successfully deleted");
                }});
                
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new HashMap<String, String>() {{
                    put("message", "Error deleting blog: " + e.getMessage());
                }});
        }
    }
}
