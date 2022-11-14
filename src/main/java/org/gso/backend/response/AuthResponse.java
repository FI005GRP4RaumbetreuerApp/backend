package org.gso.backend.response;

import lombok.Data;

@Data
public class AuthResponse {
    private String uuid;
    private String username;
    private String token;
}
