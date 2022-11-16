package org.gso.backend.repository;

import org.gso.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    @Query("SELECT user FROM User user WHERE user.password_reset_code = ?1")
    Optional<User> findByPassword_reset_code(String reset_code);
}
