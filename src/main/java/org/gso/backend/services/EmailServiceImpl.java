package org.gso.backend.services;

import org.gso.backend.model.EmailDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.concurrent.Executors;

@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;

    public void sendSimpleMail(EmailDetails details) {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                MimeMessage mimeMessage = javaMailSender.createMimeMessage();
                MimeMessageHelper mimeMessageHelper;

                try {
                    mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
                    mimeMessageHelper.setFrom(sender);
                    mimeMessageHelper.setTo(details.getRecipient());
                    mimeMessageHelper.setText(details.getMsgBody(), true);
                    mimeMessageHelper.setSubject(details.getSubject());
                    javaMailSender.send(mimeMessage);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void sendMailWithAttachment(EmailDetails details) {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                MimeMessage mimeMessage = javaMailSender.createMimeMessage();
                MimeMessageHelper mimeMessageHelper;

                try {
                    mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
                    mimeMessageHelper.setFrom(sender);
                    mimeMessageHelper.setTo(details.getRecipient());
                    mimeMessageHelper.setText(details.getMsgBody());
                    mimeMessageHelper.setSubject(
                            details.getSubject());

                    FileSystemResource file = new FileSystemResource(new File(details.getAttachment()));

                    mimeMessageHelper.addAttachment(file.getFilename(), file);

                    javaMailSender.send(mimeMessage);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
