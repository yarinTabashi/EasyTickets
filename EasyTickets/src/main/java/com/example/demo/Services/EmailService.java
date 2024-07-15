package com.example.demo.Services;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Service class for sending emails and handling email-related operations.
 */
@Service
public class EmailService {
    private String emailUsername = "ticketsa902@gmail.com";
    private String emailPassword = "nikajhwpgnspmvgb";

    /**
     * Sends an email using SMTP protocol with Gmail settings.
     *
     * @param to          Email address of the recipient.
     * @param title       Subject of the email.
     * @param htmlContent HTML content of the email body.
     * @return true if the email was sent successfully, false otherwise.
     */
    public boolean sendEmail(String to, String title, String htmlContent) {
        String from = emailUsername;

        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        // Disable SSL certificate validation (not recommended for production)
        properties.put("mail.smtp.ssl.trust", "*");

        // Get the Session object.
        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                // Use an app password if you have two-factor authentication enabled
                return new PasswordAuthentication(from, emailPassword);
            }
        });

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            // Set Subject: header field
            message.setSubject(title);

            // Now set the actual message
            message.setContent(htmlContent, "text/html");

            // Send message
            Transport.send(message);
            return true;
        } catch (MessagingException mex) {
            mex.printStackTrace();
            return false;
        }
    }

    /**
     * Reads HTML content from a resource file in the classpath and returns it as a String.
     * This method facilitates sending emails using HTML templates.
     *
     * @param resourceName Name of the resource file to read.
     * @return String containing the content of the HTML resource file.
     * @throws IOException if there is an error reading the resource file.
     */
    public String readHtmlFromResource(String resourceName) throws IOException {
        ClassPathResource resource = new ClassPathResource(resourceName);
        try (InputStream inputStream = resource.getInputStream()) {
            byte[] bdata = FileCopyUtils.copyToByteArray(inputStream);
            return new String(bdata, StandardCharsets.UTF_8);
        }
    }

    /**
     * Sends an OTP (One-Time Password) email to the specified recipient.
     *
     * @param to      Email address of the recipient.
     * @param otpCode OTP code to be included in the email.
     * @return true if the OTP email was sent successfully, false otherwise.
     * @throws IOException if there is an error reading the OTP email template.
     */
    public boolean sendOtpEmail(String to, String otpCode) throws IOException {
        try
        {
            String htmlContent = readHtmlFromResource("emailTemplates/VerificationTemplate.html");
            String modifiedHtmlContent = htmlContent.replace("{{OTP_PLACEHOLDER}}", otpCode);
            boolean isSucceed = sendEmail(to, "Email verification", modifiedHtmlContent);
            if (!isSucceed){
                return false;
            }
        }
        catch (Exception e){
            return false;
        }
        return true;
    }
}
