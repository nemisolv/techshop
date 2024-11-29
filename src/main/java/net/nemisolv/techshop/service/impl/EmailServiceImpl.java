package net.nemisolv.techshop.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nemisolv.techshop.entity.User;
import net.nemisolv.techshop.service.EmailService;
import net.nemisolv.techshop.util.CommonUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static net.nemisolv.techshop.core._enum.EmailTemplates.CUSTOMER_REGISTRATION;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${app.mail.from}")
    private String senderEmail;

    @Override
    @Async
    public void sendRegistrationEmail(User recipient, String token) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, UTF_8.name());
        messageHelper.setFrom(senderEmail);

        final String templateName = CUSTOMER_REGISTRATION.getTemplate();

        Map<String, Object> variables = new HashMap<>();
        variables.put("customerName", recipient.getFirstName() + " " + recipient.getLastName());
        String urlVerification = CommonUtil.buildEmailUrl("/verify-email", token);
        variables.put("url", urlVerification);

        Context context = new Context();
        context.setVariables(variables);
        messageHelper.setSubject(CUSTOMER_REGISTRATION.getSubject());

        try {
            String htmlTemplate = templateEngine.process(templateName, context);
            messageHelper.setText(htmlTemplate, true);

            messageHelper.setTo(recipient.getEmail());
            mailSender.send(message);
            log.info("INFO - Email successfully sent to {} with template {} ", recipient.getEmail(), templateName);
        } catch (MessagingException e) {
            log.warn("WARNING - Cannot send Email to {} ", recipient.getEmail());
        }
    }



}
