package org.gso.backend.repository;

import org.gso.backend.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RaumRepository extends JpaRepository<Room, Long> {
    @Query("SELECT room FROM Room room WHERE room.id = ?1")
    Optional<Room> getById(String id);
}
