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
import com.ng.service.UserSessionService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/v1/auth/")
public class AuthController
{
	private static final Logger log = LoggerFactory.getLogger(AuthController.class);

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserSessionService sessionService;

	@PostMapping("/signup")
	public ResponseEntity<MyUser> signup(@RequestBody MyUser user)
	{
		System.out.println("roles: " + user.getRoles());
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setRoles(user.getRoles());
		MyUser savedUser = userRepository.save(user);
		return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
	}

//	@PostMapping("/login")
//	public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials,
//			HttpServletResponse response)
//	{
//		String username = credentials.get("username");
//		String password = credentials.get("password");
//		log.info("Login attempt for username: {}", username);
//
//		Optional<MyUser> userOpt = userRepository.findByusername(username);
//		if (userOpt.isPresent())
//		{
//			MyUser user = userOpt.get();
//			if (passwordEncoder.matches(password, user.getPassword()))
//			{
//				log.info("Login successful for user: {}", username);
//				String accessToken = jwtUtil.generateAccessToken(username);
//				String refreshToken = jwtUtil.generateRefreshToken(username);
//
//				sessionService.createOrUpdateSession(username, refreshToken);
//
//				Cookie cookie = new Cookie("refreshToken", refreshToken);
//				cookie.setHttpOnly(true);
//				cookie.setSecure(true);
//				cookie.setPath("/");
//				cookie.setMaxAge(60 * 60 * 24);
//				response.addCookie(cookie);
//
//				Map<String, Object> resp = new HashMap<>();
//				resp.put("accessToken", accessToken);
//				resp.put("refreshToken", refreshToken);
//				resp.put("username", user.getUsername());
//				resp.put("roles", user.getRoles());
//				resp.put("expiresIn", jwtUtil.getExpiration(accessToken));
//
//				return ResponseEntity.ok(resp);
//			} else
//			{
//				log.warn("Invalid password for user: {}", username);
//			}
//		} else
//		{
//			log.warn("User not found: {}", username);
//		}
//		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid credentials"));
//	}

	@PostMapping("/login")
	public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials,
			HttpServletResponse response)
	{
		String username = credentials.get("username");
		String password = credentials.get("password");
		log.info("Login attempt for username: {}", username);

		Optional<MyUser> userOpt = userRepository.findByusername(username);
		if (userOpt.isPresent())
		{
			MyUser user = userOpt.get();
			if (passwordEncoder.matches(password, user.getPassword()))
			{
				log.info("Login successful for user: {}", username);

				String accessToken = jwtUtil.generateAccessToken(username);
				String refreshToken = jwtUtil.generateRefreshToken(username);

				sessionService.createOrUpdateSession(username, refreshToken);

				Cookie cookie = new Cookie("refreshToken", refreshToken);
				cookie.setHttpOnly(true);
				cookie.setSecure(true); 
				cookie.setPath("/");
				cookie.setMaxAge(60 * 60 * 24); 
				response.addCookie(cookie);

				Map<String, Object> resp = new HashMap<>();
				resp.put("accessToken", accessToken);
				resp.put("username", user.getUsername());
				resp.put("roles", user.getRoles());
				resp.put("expiresIn", jwtUtil.getExpiration(accessToken));

				return ResponseEntity.ok(resp);
			} else
			{
				log.warn("Invalid password for user: {}", username);
			}
		} else
		{
			log.warn("User not found: {}", username);
		}

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid credentials"));
	}

//	@PostMapping("/refresh")
//	public ResponseEntity<Map<String, Object>> refresh(HttpServletRequest request, HttpServletResponse response)
//	{
//		Cookie[] cookies = request.getCookies();
//		String refreshToken = null;
//		if (cookies != null)
//		{
//			for (Cookie cookie : cookies)
//			{
//				if ("refreshToken".equals(cookie.getName()))
//				{
//					refreshToken = cookie.getValue();
//				}
//			}
//		}
//		else
//		{
//			log.info("Invalid cookies..");
//		}
//
//		if (refreshToken == null || !sessionService.isSessionActive(refreshToken, 10))
//		{
//			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//					.body(Map.of("message", "Session expired or inactive. Please login again."));
//		}
//
//		String username = jwtUtil.extractUsername(refreshToken);
//		String newAccessToken = jwtUtil.generateAccessToken(username);
//		String newRefreshToken = jwtUtil.generateRefreshToken(username);
//
//		sessionService.createOrUpdateSession(username, newRefreshToken);
//
//		Cookie cookie = new Cookie("refreshToken", newRefreshToken);
//		cookie.setHttpOnly(true);
//		cookie.setSecure(true);
//		cookie.setPath("/");
//		cookie.setMaxAge(60 * 60 * 24);
//		response.addCookie(cookie);
//
//		return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
//	}

	@PostMapping("/refresh")
	public ResponseEntity<Map<String, Object>> refresh(HttpServletRequest request, HttpServletResponse response)
	{
		Cookie[] cookies = request.getCookies();
		String refreshToken = null;

		if (cookies != null)
		{
			for (Cookie cookie : cookies)
			{
				if ("refreshToken".equals(cookie.getName()))
				{
					refreshToken = cookie.getValue();
					break;
				}
			}
		}

		if (refreshToken == null || !sessionService.isSessionActive(refreshToken, 10))
		{
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Map.of("message", "Session expired or inactive. Please login again."));
		}

		String username = jwtUtil.extractUsername(refreshToken);
		String newAccessToken = jwtUtil.generateAccessToken(username);
		String newRefreshToken = jwtUtil.generateRefreshToken(username);

		sessionService.createOrUpdateSession(username, newRefreshToken);

		Cookie cookie = new Cookie("refreshToken", newRefreshToken);
		cookie.setHttpOnly(true);
		cookie.setSecure(true); // set true in production with HTTPS
		cookie.setPath("/");
		cookie.setMaxAge(60 * 60 * 24);
		response.addCookie(cookie);

		return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
	}

	@PostMapping("/logout")
	public ResponseEntity<Map<String, String>> logOut(@RequestBody Map<String, String> request)
	{
		String username = request.get("username");
		sessionService.removeSession(username);

		return ResponseEntity.ok(Map.of("message", "Logout successfully"));
	}

}
