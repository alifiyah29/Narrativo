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
        try {
            Blog savedBlog = blogService.createBlog(blog, userDetails.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedBlog);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping
    public ResponseEntity<List<Blog>> getAllBlogs() {
        try {
            List<Blog> blogs = blogService.getAllBlogs();
            return ResponseEntity.ok(blogs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Blog> getBlogById(@PathVariable Long id) {
        Optional<Blog> blog = blogService.getBlogById(id);
        return blog.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}") 
    public ResponseEntity<List<Blog>> getBlogsByUser(@PathVariable Long userId) {
        try {
            List<Blog> blogs = blogService.getBlogsByUser(userId);
            return ResponseEntity.ok(blogs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/visibility/{visibility}")
    public ResponseEntity<List<Blog>> getBlogsByVisibility(
        @PathVariable String visibility,
        @AuthenticationPrincipal UserDetails userDetails) {
    
        List<Blog> blogs;
    
        if ("ALL".equalsIgnoreCase(visibility)) {
            blogs = blogService.getAllBlogs(); // Return all blogs
        } else {
            try {
                Blog.Visibility enumVisibility = Blog.Visibility.valueOf(visibility.toUpperCase());
                blogs = blogService.getBlogsByVisibility(enumVisibility, userDetails.getUsername());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(null); // Invalid visibility
            }
        }
    
        return ResponseEntity.ok(blogs);
    }
    

    @PutMapping("/{id}/visibility")
    public ResponseEntity<?> updateBlogVisibility(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long id,
        @RequestParam Blog.Visibility visibility) {
        Optional<Blog> existingBlog = blogService.getBlogById(id);
        if (existingBlog.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(createMessage("Blog not found with id: " + id));
        }
        Blog blog = existingBlog.get();
        if (!blog.getUser().getUsername().equals(userDetails.getUsername())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(createMessage("You don't have permission to update this blog"));
        }
        try {
            blog.setVisibility(visibility);
            blog.setUpdatedAt(LocalDateTime.now());
            Blog savedBlog = blogService.updateBlog(blog);
            return ResponseEntity.ok(savedBlog);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createMessage("Error updating blog visibility: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}/view")
    public ResponseEntity<Blog> incrementViews(@PathVariable Long id) {
        Blog blog = blogService.incrementViews(id);
        return ResponseEntity.ok(blog);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBlog(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long id,
        @RequestBody Blog updatedBlog) {
        Optional<Blog> existingBlog = blogService.getBlogById(id);
        if (existingBlog.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(createMessage("Blog not found with id: " + id));
        }
        Blog blog = existingBlog.get();
        if (!blog.getUser().getUsername().equals(userDetails.getUsername())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(createMessage("You don't have permission to update this blog"));
        }
        try {
            blog.setTitle(updatedBlog.getTitle());
            blog.setContent(updatedBlog.getContent());
            blog.setVisibility(updatedBlog.getVisibility());
            blog.setUpdatedAt(LocalDateTime.now());
            Blog savedBlog = blogService.updateBlog(blog);
            return ResponseEntity.ok(savedBlog);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createMessage("Error updating blog: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBlog(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long id) {
        Optional<Blog> existingBlog = blogService.getBlogById(id);
        if (existingBlog.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(createMessage("Blog not found with id: " + id));
        }
        Blog blog = existingBlog.get();
        if (!blog.getUser().getUsername().equals(userDetails.getUsername())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(createMessage("You don't have permission to delete this blog"));
        }
        try {
            blogService.deleteBlog(id);
            return ResponseEntity.ok(createMessage("Blog successfully deleted"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createMessage("Error deleting blog: " + e.getMessage()));
        }
    }

    private HashMap<String, String> createMessage(String message) {
        HashMap<String, String> response = new HashMap<>();
        response.put("message", message);
        return response;
    }
}