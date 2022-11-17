package org.gso.backend.repository;

import org.gso.backend.entity.GeräteTyp;
import org.gso.backend.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface GeräteTypRepository extends JpaRepository<GeräteTyp, Long> {
    @Query("SELECT geraet FROM GeräteTyp geraet WHERE geraet.id = ?1")
    Optional<GeräteTyp> getById(long id);
}
