package org.gso.backend.repository;

import org.gso.backend.entity.Room;
import org.gso.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RaumRepository extends JpaRepository<Room, Long> {
    @Query("SELECT room FROM Room room WHERE room.id = ?1 ORDER BY room.id ASC")
    Optional<Room> getById(String id);

    @Query("SELECT room FROM Room room WHERE room.raumbetreuer = ?1 ORDER BY room.id ASC")
    List<Room> getById(User user);

    @Query("SELECT room FROM Room room ORDER BY room.id ASC")
    List<Room> findAll();
}
