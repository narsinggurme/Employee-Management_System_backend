package com.ng.controller;

import com.ng.service.EmailService;
import com.ng.service.OtpService;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/v1/otp")
public class OtpController
{

	@Autowired
	private OtpService otpService;

	@Autowired
	private EmailService emailService;

	// ---------- EMAIL OTP ----------
	@PostMapping("/send-email")
	public ResponseEntity<Map<String, String>> sendEmailOtp(@RequestBody Map<String, String> request)
	{
		String email = request.get("email");
		System.out.println("api email: " + email);
		String otp = otpService.generateEmailOtp(email);
		emailService.sendOtpMail(email, otp);

		return ResponseEntity
				.ok(Map.of("status", "success", "message", "OTP sent successfully to email", "email", email));
	}

	@PostMapping("/verify-email")
	public ResponseEntity<Map<String, String>> verifyEmailOtp(@RequestBody Map<String, String> request)
	{
		String email = request.get("email");
		String otp = request.get("otp");
System.out.println("email|otp" + email + "|"+ otp);
		boolean isValid = otpService.verifyEmailOtp(email, otp);

		if (isValid)
		{
			return ResponseEntity
					.ok(Map.of("status", "success", "message", "OTP verification successful", "email", email));
		} else
		{
			return ResponseEntity.badRequest()
					.body(Map.of("status", "error", "message", "Invalid or expired OTP", "email", email));
		}
	}

	// ---------- PHONE OTP ----------
	@PostMapping("/send-phone")
	public ResponseEntity<Map<String, String>> sendPhoneOtp(@RequestBody Map<String, String> request)
	{
		String number = request.get("phone");
		otpService.sendOtpToPhone(number); // currently printing OTP in console

		return ResponseEntity
				.ok(Map.of("status", "success", "message", "OTP sent successfully to phone", "phone", number));
	}

	@PostMapping("/verify-phone")
	public ResponseEntity<Map<String, String>> verifyPhoneOtp(@RequestBody Map<String, String> request)
	{
		String number = request.get("phone");
		String otp = request.get("otp");
		boolean result = otpService.verifyPhoneOtp(number, otp);

		if (result)
		{
			return ResponseEntity
					.ok(Map.of("status", "success", "message", "OTP verification successful", "phone", number));
		} else
		{
			return ResponseEntity.badRequest()
					.body(Map.of("status", "error", "message", "Invalid OTP", "phone", number));
		}
	}
}
