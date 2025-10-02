package com.ng.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.ng.controller.EmployeeController;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService
{
	private static final Logger log = LoggerFactory.getLogger(EmployeeController.class);

	@Autowired
	private JavaMailSender mailSender;

	public void sendForgotPasMailLink(String to, String resetLink)
	{
		try
		{
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);

			helper.setTo(to);
			helper.setSubject("Password Reset Link");
			helper.setText(
					"Please click the link to reset your password: <a href='" + resetLink + "'>Reset Password</a>",
					true);

			mailSender.send(message);
		} catch (Exception e)
		{
			log.error("Error sending password reset email to {}", to, e);
		}
	}

	public void sendOtpMail(String to, String otp)
	{
		try
		{
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);

			helper.setTo(to);
			helper.setSubject("Email Verification - OTP");
			helper.setText("<p>Hello,</p>" + "<p>Your OTP for email verification is:</p>" + "<h2 style='color:green;'>"
					+ otp + "</h2>" + "<p>This OTP is valid for 5 minutes.</p>", true);

			mailSender.send(message);
			log.info("OTP email sent successfully to {}", to);
		} catch (Exception e)
		{
			log.error("Error sending OTP email to {}", to, e);
		}
	}
}