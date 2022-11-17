package org.gso.backend.repository;

import org.gso.backend.entity.Meldung;
import org.gso.backend.entity.Room;
import org.gso.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MeldungsRepository extends JpaRepository<Meldung, Long> {
    @Query("SELECT meldungen FROM Meldung meldungen WHERE meldungen.created_by = ?1")
    List<Meldung> findAllByCreated_by(User user);
    @Query("SELECT meldungen FROM Meldung meldungen WHERE meldungen.room = ?1")
    List<Meldung> findAllByRoomId(Room room);

}
