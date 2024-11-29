package net.nemisolv.techshop.service;

import jakarta.mail.MessagingException;
import net.nemisolv.techshop.entity.User;

public interface EmailService {
    void sendRegistrationEmail(User user, String token) throws MessagingException;
}
