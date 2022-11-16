package org.gso.backend.services;

import org.gso.backend.model.EmailDetails;

public interface EmailService {
    String sendSimpleMail(EmailDetails details);
    String sendMailWithAttachment(EmailDetails details);
}
