package com.ng.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService
{
	@Autowired
	private JavaMailSender mailSender;
	
//	public void sendForgotPasMailLink(String to, String resetLink)
//	{
//		SimpleMailMessage mailMessage = new SimpleMailMessage();
//		mailMessage.setTo(to);
//		mailMessage.setSubject("Reset your password");
//		mailMessage.setText("Click the link to reset your password: " + resetLink);
//		
//		mailSender.send(mailMessage);
//	}
	
	public void sendForgotPasMailLink(String to, String resetLink)
	{
		MimeMessage message = mailSender.createMimeMessage();

		MimeMessageHelper helper;
		try
		{
			helper = new MimeMessageHelper(message, true);
			helper.setTo(to);
			helper.setSubject("Password Reset Link");
			helper.setText("Click the link to reset your password: " + resetLink, true);

		} 
		catch (MessagingException e)
		{
			e.printStackTrace();
		}

		mailSender.send(message);
	}

}
