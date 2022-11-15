package org.gso.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.gso.backend.enums.Meldungstyp;
import org.gso.backend.enums.Status;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "meldungen")
public class Meldung {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Room room;
    @OneToOne
    private GeräteTyp geraete_typ;
    private Meldungstyp meldungstyp;
    private String description;
    private Status status;
    private Timestamp created_at;
    @ManyToOne
    private User created_by;
    private Timestamp updated_at;
    @ManyToOne
    private User updated_by;


}