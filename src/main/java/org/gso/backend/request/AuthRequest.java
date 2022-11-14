package org.gso.backend.request;

import lombok.Data;

@Data
public class AuthRequest {
    private String uuid;
    private String password;

}