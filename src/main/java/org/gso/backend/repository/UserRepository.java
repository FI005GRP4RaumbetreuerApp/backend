package org.gso.backend.repository;

import org.gso.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUuid(String uuid);
    Optional<User> findByName(String name);
}
