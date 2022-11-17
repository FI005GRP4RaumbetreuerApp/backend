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
    private String geraete_id;
    private String meldungs_typ;
    private String status;


}
