package com.narrativo.services;

import com.narrativo.models.Blog;
import com.narrativo.models.User;
import com.narrativo.repositories.BlogRepository;
import com.narrativo.repositories.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlogServiceImpl implements BlogService {

    private final BlogRepository blogRepository;
    private final UserRepository userRepository;

    @Override
    public Blog createBlog(Blog blog, String username) {
        // Fetch user from the database
        User author = userRepository.findByUsername(username);
        if (author == null) {
            throw new RuntimeException("User not found");
        }

        // Set author and timestamps
        blog.setAuthor(author);
        blog.setCreatedAt(LocalDateTime.now());
        blog.setUpdatedAt(LocalDateTime.now());

        return blogRepository.save(blog);
    }

    @Override
    public List<Blog> getAllBlogs() {
        System.out.println("Fetching all blogs..."); //Debugging
        return blogRepository.findAll();
    }

    @Override
    public List<Blog> getBlogsByAuthor(Long authorId) {
        return blogRepository.findByAuthorId(authorId);
    }

    @Override
    public List<Blog> getBlogsByVisibility(Blog.Visibility visibility, String username) {
        User user = userRepository.findByUsername(username);
        List<User> friends = userRepository.findByFriendsContaining(user);
    
        return blogRepository.findAll().stream()
            .filter(blog -> blog.getVisibility() == Blog.Visibility.PUBLIC
                    || (blog.getVisibility() == Blog.Visibility.FRIENDS_ONLY && friends.contains(blog.getAuthor()))
                    || (blog.getVisibility() == Blog.Visibility.PRIVATE && blog.getAuthor().equals(user))
                    || user.getRole() == User.Role.ADMIN)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<Blog> getBlogById(Long id) {
        return blogRepository.findById(id);
    }

    @Override
    public Blog updateBlog(Blog blog) {
        blog.setUpdatedAt(LocalDateTime.now());
        return blogRepository.save(blog);
    }

    @Override
    @Transactional
    public void deleteBlog(Long id) {
        Optional<Blog> blog = blogRepository.findById(id);
        if (blog.isPresent()) {
            blogRepository.delete(blog.get());
        } else {
            throw new RuntimeException("Blog not found with id: " + id);
        }
    }

    @Transactional
    public Blog incrementViews(Long id) {
        Optional<Blog> blogOptional = blogRepository.findById(id);
        if (blogOptional.isPresent()) {
            Blog blog = blogOptional.get();
            blog.setViews(blog.getViews() + 1); // Increment views
            return blogRepository.save(blog);
        } else {
            throw new RuntimeException("Blog not found with id: " + id);
        }
    }    
}