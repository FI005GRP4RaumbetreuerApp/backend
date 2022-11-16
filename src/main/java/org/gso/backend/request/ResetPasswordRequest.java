package org.gso.backend.request;

import lombok.Data;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Data
public class ResetPasswordRequest {
    private String new_password;
    private String new_password_conformation;
    private String reset_code;

    public String getPasswordEncrypted(){
        return new BCryptPasswordEncoder().encode(new_password);
    }

    public boolean isValid(){
        return new_password.equals(new_password_conformation);
    }
}
