package org.gso.backend.request;

import lombok.Data;

@Data
public class RefreshRequest {
    private String access_token;
    private String refresh_token;
}
