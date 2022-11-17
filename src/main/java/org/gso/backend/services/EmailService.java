package org.gso.backend.services;

import org.gso.backend.model.EmailDetails;

public interface EmailService {
    void sendSimpleMail(EmailDetails details);
    void sendMailWithAttachment(EmailDetails details);
}
