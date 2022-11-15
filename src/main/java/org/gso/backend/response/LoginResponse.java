package org.gso.backend.response;

import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Data
@Builder
public class LoginResponse {
    private String access_token;
    private String refresh_token;
    private String email;
    private Collection<? extends GrantedAuthority> roles;
}
