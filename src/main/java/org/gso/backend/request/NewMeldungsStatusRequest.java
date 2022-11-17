package org.gso.backend.request;

import lombok.Data;

@Data
public class NewMeldungsStatusRequest {
    private long meldungs_id;
    private String status;
}
