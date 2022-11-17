package org.gso.backend.request;

import lombok.Data;
import org.gso.backend.entity.GeräteTyp;
import org.gso.backend.enums.Meldungstyp;
import org.gso.backend.enums.Status;

@Data
public class MeldungsRequest {
    private String raum_id;
    private long geraete_typ_id;
    private String description;
    private long created_by_id;
    private String geraete_id;
    private Meldungstyp meldungs_typ;
    private Status status;


}
