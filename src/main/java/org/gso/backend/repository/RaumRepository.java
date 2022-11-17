package org.gso.backend.repository;

import org.gso.backend.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RaumRepository extends JpaRepository<Room, Long> {
}
