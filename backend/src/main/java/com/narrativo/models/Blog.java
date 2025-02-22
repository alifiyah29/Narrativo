package com.narrativo.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
public class Blog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private Integer views = 0;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference // Prevents infinite recursion
    private User user; // This is the field referenced by 'mappedBy'

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private Visibility visibility = Visibility.PUBLIC;

    @Column(nullable = false)
    private Boolean deleted = false;

    public enum Visibility {
        PUBLIC, PRIVATE, FRIENDS_ONLY
    }
}