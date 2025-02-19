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

    // Create a new blog
    @PostMapping
    public ResponseEntity<Blog> createBlog(@AuthenticationPrincipal UserDetails userDetails, @RequestBody Blog blog) {
        try {
            Blog savedBlog = blogService.createBlog(blog, userDetails.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedBlog);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Get all blogs
    @GetMapping
    public ResponseEntity<List<Blog>> getAllBlogs() {
        try {
            List<Blog> blogs = blogService.getAllBlogs();
            System.out.println("Returning blogs: " + blogs.size()); // Debug
            return ResponseEntity.ok(blogs); // Ensure only one response is returned
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get a blog by ID
    @GetMapping("/{id}")
    public ResponseEntity<Blog> getBlogById(@PathVariable Long id) {
        Optional<Blog> blog = blogService.getBlogById(id);
        return blog.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Get blogs by author
    @GetMapping("/user/{authorId}")
    public ResponseEntity<List<Blog>> getBlogsByAuthor(@PathVariable Long authorId) {
        try {
            List<Blog> blogs = blogService.getBlogsByAuthor(authorId);
            return ResponseEntity.ok(blogs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get blogs by visibility
    @GetMapping("/visibility/{visibility}")
    public ResponseEntity<List<Blog>> getBlogsByVisibility(
        @PathVariable Blog.Visibility visibility,
        @AuthenticationPrincipal UserDetails userDetails) {
        try {
            List<Blog> blogs = blogService.getBlogsByVisibility(visibility, userDetails.getUsername());
            return ResponseEntity.ok(blogs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //Update blog visibility
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
        if (!blog.getAuthor().getUsername().equals(userDetails.getUsername())) {
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

    
    //increment the views of a blog
    @PutMapping("/{id}/view")
    public ResponseEntity<Blog> incrementViews(@PathVariable Long id) {
        Blog blog = blogService.incrementViews(id);
        return ResponseEntity.ok(blog);
    }    

    // Update a blog
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
        if (!blog.getAuthor().getUsername().equals(userDetails.getUsername())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createMessage("You don't have permission to update this blog"));
        }

        try {
            // Update allowed fields
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

    // Delete a blog
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
        if (!blog.getAuthor().getUsername().equals(userDetails.getUsername())) {
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

    // Helper method to create a response message
    private HashMap<String, String> createMessage(String message) {
        HashMap<String, String> response = new HashMap<>();
        response.put("message", message);
        return response;
    }
}
