package com.ng.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ng.config.JwtUtil;
import com.ng.entity.MyUser;
import com.ng.repository.UserRepository;
import com.ng.service.EmailService;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/v1/password/")
public class PasswordController
{
	private static final Logger log = LoggerFactory.getLogger(PasswordController.class);

	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private EmailService emailService;

	@PostMapping("/forgot-password/reset")
	public ResponseEntity<Map<String, String>> resetPassword(@RequestBody Map<String, String> request)
	{
		log.info("Reset link sending..");
		String token = request.get("token");
		String newPassword = request.get("password");

		String username;
		try
		{
			username = jwtUtil.extractUsername(token);
		}
		catch (Exception e)
		{
			log.warn("Invalid token...");
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Invalid or expired token"));
		}

		Optional<MyUser> userOpt = userRepository.findByusername(username);
		if (userOpt.isPresent())
		{
			MyUser user = userOpt.get();
			user.setPassword(passwordEncoder.encode(newPassword));
			userRepository.save(user);
			return ResponseEntity.ok(Map.of("message", "Password updated successfully"));
		} 
		else
		{
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found"));
		}
	}

	@PostMapping("/forgot-password/send-link")
	public ResponseEntity<Map<String, String>> resetPasswordLink(@RequestBody Map<String, String> request)
	{
		String username = request.get("username");

		Optional<MyUser> userDetails = userRepository.findByusername(username);

		if (userDetails.isPresent())
		{
			MyUser myUser = userDetails.get();
			String email = myUser.getEmail();
			System.out.println("email:" + email);
			if (email == null || !email.contains("@"))
			{
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Invalid email for user"));
			}

			String token = jwtUtil.generateAccessToken(username);
			String resetLink = "http://localhost:4200/forgot-password?token=" + token;
//			String resetLink = "https://emsappbynarsing.netlify.app/forgot-password?token=" + token;
			emailService.sendForgotPasMailLink(email, resetLink);

			return ResponseEntity.ok(Map.of("message", "Reset link sent to email"));
		}
		else
		{
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User Not Found.."));

		}
	}
	
	@PostMapping("/forgot-password/check-username")
	public ResponseEntity<Map<String, String>> checkUserName(@RequestBody Map<String, String> request)
	{
		String username = request.get("username");
		Map<String, String> response = new HashMap<String, String>();
		Optional<MyUser> userOpt = userRepository.findByusername(username);
		if (userOpt.isPresent())
		{
			response.put("message", "username is exist");
			log.info("Username is exist");
			return ResponseEntity.ok(response);
		} else
		{
			log.warn("Username not found");
			response.put("message", "username is not found");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}
	}


}
