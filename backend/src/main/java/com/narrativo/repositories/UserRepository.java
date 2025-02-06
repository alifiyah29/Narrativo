package com.narrativo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.narrativo.models.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
