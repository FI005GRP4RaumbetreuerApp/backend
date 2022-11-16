package org.gso.backend.request;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Data
public class RegistrationRequest {
    String email;
    String vornamen;
    String nachnamen;
    String kürzel;
    String password;

    public String getPasswordEncrypted(){
        return new BCryptPasswordEncoder().encode(password);
    }

    public boolean isValid(){
        if(StringUtils.isEmpty(email)) return false;
        if(StringUtils.isEmpty(vornamen)) return false;
        if(StringUtils.isEmpty(nachnamen)) return false;
        if(StringUtils.isEmpty(kürzel)) return false;
        if(StringUtils.isEmpty(password)) return false;
        if(!email.contains("@")) return false;

        return true;
    }
}
