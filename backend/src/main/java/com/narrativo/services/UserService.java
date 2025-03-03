package com.narrativo.services;

import com.narrativo.models.User;
import com.narrativo.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword())); 
        return userRepository.save(user);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }    

    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(userRepository.findByUsername(username));
    }    

    public void createInitialAdmin() {
        if (userRepository.count() == 0) { // Only create admin if no users exist
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@narrativo.com");
            admin.setPassword(passwordEncoder.encode("admin123")); // Set a strong default password
            admin.setRole(User.Role.ADMIN);
            userRepository.save(admin);
        }
    } 
}
