package org.gso.backend.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RefreshResponse {
    private String access_token;
}
