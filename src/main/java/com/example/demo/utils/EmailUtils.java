package com.example.demo.utils;

import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailUtils {

	@Autowired
	private JavaMailSender emailSender;

	@Value("${spring.mail.username}")
	private String sender;

	public String sendSimpleMessage(String to, String subject, String text, List<String> list) {
		SimpleMailMessage message = new SimpleMailMessage();

		// Setting up necessary details
		message.setFrom(sender);
		message.setTo(to);
		message.setSubject(subject);
		message.setText(text);

		if (list.size() > 0) {
			list.forEach(cc -> {
				message.setCc(cc);
			});
		}

		// Sending the mail
		emailSender.send(message);
		return "Mail Sent Successfully...";
	}

	public void forgotPassword(String to, String subject, String password) throws MessagingException {
		MimeMessage message = emailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);
		helper.setFrom(sender);
		helper.setTo(to);
		helper.setSubject(subject);
		String htmlMsg = "<p><b>Your Login details for Cafe Management System</b><br><b>Email: </b> " + to
				+ " <br><b>Password: </b> " + password
				+ "<br><a href=\"http://localhost:4200/\">Click here to login</a></p>";
		message.setContent(htmlMsg, "text/html");
		emailSender.send(message);
	}
	
	
    public void sendOrderConfirmationEmail(String recipientEmail, String orderNumber, String orderStatus) {

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            helper.setTo(recipientEmail);
            helper.setSubject("Order Confirmation - " + orderNumber);

            String text = "<p>Dear Customer,</p>"
                    + "<p>We would like to confirm that your order <strong>" + orderNumber + "</strong> has been "
                    + "<strong>" + orderStatus + "</strong>.</p>"
                    + "<p>Thank you for shopping with us!</p>";

            helper.setText(text, true);

            emailSender.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
