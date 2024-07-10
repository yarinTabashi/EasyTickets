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

@Service
public class EmailService {
//    @Value("${email.username}")
//    private String emailUsername;
//
//    @Value("${email.password}")
//    private String emailPassword;
    private String emailUsername = "ticketsa902@gmail.com";
    private String emailPassword = "nikajhwpgnspmvgb";

    public boolean sendEmail(String to, String title, String htmlContent) {
        // Sender's email ID needs to be mentioned
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

    public String readHtmlFromResource(String resourceName) throws IOException {
        ClassPathResource resource = new ClassPathResource(resourceName);
        try (InputStream inputStream = resource.getInputStream()) {
            byte[] bdata = FileCopyUtils.copyToByteArray(inputStream);
            return new String(bdata, StandardCharsets.UTF_8);
        }
    }

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
