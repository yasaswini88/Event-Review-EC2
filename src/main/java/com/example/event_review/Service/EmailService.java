package com.example.event_review.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender javaMailSender;

    public void sendSimpleEmail(String to, String subject, String text) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(to);
        mailMessage.setSubject(subject);
        mailMessage.setText(text);
        javaMailSender.send(mailMessage);
    }

    // New method for HTML email
    public void sendEmailWithLink(String to, String subject, String link, String textBody) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);

            // You can build an HTML body that has a clickable link
            String htmlContent = "<p>" + textBody + "</p>"
                + "<p>Click <a href=\"" + link + "\">here</a> to view more details.</p>";

            helper.setText(htmlContent, true); // The 'true' indicates it's HTML content

            javaMailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}