package com.narrativo.controllers;

import com.narrativo.models.User;
import com.narrativo.payloads.LoginRequest;
import com.narrativo.payloads.RegisterRequest;
import com.narrativo.repositories.UserRepository;
import com.narrativo.security.JwtUtil;
import com.narrativo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Autowired
    public AuthController(
            UserService userService,
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil,
            UserRepository userRepository
    ) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setRole(User.Role.USER); // Assign default role
        userService.createUser(user);

        Map<String, String> response = new HashMap<>();
        response.put("message", "User registered successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register-admin")
    @PreAuthorize("hasRole('ADMIN')") // Only admins can access this endpoint
    public ResponseEntity<Map<String, String>> registerAdmin(@RequestBody RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setRole(User.Role.ADMIN); // Assign ADMIN role
        userService.createUser(user);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Admin registered successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            // Update last login time
            User user = userRepository.findByUsername(request.getUsername());
            if (user != null) {
                user.setLastLogin(LocalDateTime.now());
                userRepository.save(user);
            }

            String token = jwtUtil.generateToken((UserDetails) authentication.getPrincipal()); // Pass UserDetails
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("username", authentication.getName());
            response.put("role", user.getRole().name()); // Include role in the response
            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid username or password"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Authentication failed: " + e.getMessage()));
        }
    }
}