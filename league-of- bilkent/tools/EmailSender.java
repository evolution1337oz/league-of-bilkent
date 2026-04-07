package tools;

import model.*;

import java.util.Properties;
import java.util.Random;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.FileInputStream;


// Email sender class to check whether user is university student. 
public class EmailSender {

    private static final String SENDER_EMAIL = "osmantustas19@gmail.com";
    private static final String SENDER_PASSWORD = "rjhp qxur omwn ecyv"; 
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final int SMTP_PORT = 587;
    private static final Random random = new Random();
   

    public static int generateCode() { // generate random verification code
       return random.nextInt(AppConstants.VERIFICATION_CODE_MIN, AppConstants.VERIFICATION_CODE_MAX);
    }

    public static boolean sendVerificationEmail(String toEmail, int code) {
        String subject = "League of Bilkent - Email Verification";
        String body = "Welcome to League of Bilkent!\n\n"
                + "Your verification code: " + code + "\n\n"
                + "Enter this code on the registration screen.";
        return sendEmail(toEmail, subject, body);
    }

    public static boolean sendPasswordResetEmail(String toEmail, int code) {
        String subject = "League of Bilkent - Password Reset";
        String body = "Your password reset request has been received.\n\n"
                + "Your reset code: " + code + "\n\n"
                + "Enter this code on the password reset screen.";
        return sendEmail(toEmail, subject, body);
    }

    private static boolean sendEmail(String toEmail, String subject, String body) {
        if (SENDER_EMAIL.isEmpty() || SENDER_PASSWORD.isEmpty()) {
            return false;
        }

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", String.valueOf(SMTP_PORT));

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
            }
        });

        // Send message
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL, "League of Bilkent"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(body);
            Transport.send(message);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
