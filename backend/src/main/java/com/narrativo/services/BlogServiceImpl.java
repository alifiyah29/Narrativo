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
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        blog.setUser(user); 
        blog.setCreatedAt(LocalDateTime.now());
        blog.setUpdatedAt(LocalDateTime.now());
        return blogRepository.save(blog);
    }

    @Override
    public List<Blog> getAllBlogs() {
        return blogRepository.findAll();
    }

    @Override
    public List<Blog> getBlogsByUser(Long userId) { 
        return blogRepository.findByUserId(userId);
    }

    @Override
    public List<Blog> getBlogsByVisibility(Blog.Visibility visibility, String username) {
        User user = userRepository.findByUsername(username);
        List<User> friends = userRepository.findByFriendsContaining(user);

        return blogRepository.findAll().stream()
            .filter(blog -> {
                switch (blog.getVisibility()) {
                    case PUBLIC:
                        return true;
                    case PRIVATE:
                        return blog.getUser().equals(user) || user.getRole() == User.Role.ADMIN;
                    case FRIENDS_ONLY:
                        return blog.getUser().equals(user)
                                || friends.contains(blog.getUser())
                                || user.getRole() == User.Role.ADMIN;
                    default:
                        return false;
                }
            })
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
        blogRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Blog incrementViews(Long id) {
        Blog blog = blogRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Blog not found with id: " + id));
        blog.setViews(blog.getViews() + 1);
        return blogRepository.save(blog);
    }
}