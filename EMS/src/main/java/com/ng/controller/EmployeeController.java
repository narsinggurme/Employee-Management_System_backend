package com.ng.controller;

import com.ng.config.JwtUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ng.entity.Employee;
import com.ng.entity.MyUser;
import com.ng.exception.ResourceNotFound;
import com.ng.repository.EmployeeRepository;
import com.ng.repository.UserRepository;
import com.ng.service.EmailService;
import com.ng.service.UserSessionService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/")
@CrossOrigin(origins = "http://localhost:4200")
public class EmployeeController
{
	private static final Logger log = LoggerFactory.getLogger(EmployeeController.class);

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserSessionService sessionService;

	@Autowired
	private EmailService emailService;

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
				resp.put("refreshToken", refreshToken);
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
				}
			}
		}
		else
		{
			log.info("Invalid cookies..");
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
		cookie.setSecure(true);
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

	@PostMapping("/signup")
	public ResponseEntity<MyUser> signup(@RequestBody MyUser user)
	{
		System.out.println("roles: " + user.getRoles());
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setRoles(user.getRoles());
		MyUser savedUser = userRepository.save(user);
		return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
	}

	@PostMapping("/empl")
	public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee)
	{
        log.info("Creating new employee");
		Employee saveEmployee = employeeRepository.save(employee);
		return new ResponseEntity<>(saveEmployee, HttpStatus.CREATED);
	}

	@GetMapping("/employees")
	public List<Employee> gelAllEmployee()
	{
        log.info("Fetching all employees");
		return employeeRepository.findAll();
	}

	@GetMapping("/employees/{id}")
	public ResponseEntity<Employee> getEmployeeById(@PathVariable Integer id)
	{
		Employee employee = employeeRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFound("Employee not found with Id: " + id));
		return ResponseEntity.ok(employee);
	}

	@PutMapping("/employees/{id}")
	public ResponseEntity<Employee> updateEmployeebyId(@PathVariable Integer id, @RequestBody Employee employeeDetails)
	{
		Employee employee = employeeRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFound("Employee not found with Id: " + id));
		employee.setName(employeeDetails.getName());
		employee.setEmail(employeeDetails.getEmail());
		employee.setPhone(employeeDetails.getPhone());
		employee.setDept(employeeDetails.getDept());

		Employee updateEmployee = employeeRepository.save(employee);
		return ResponseEntity.ok(updateEmployee);
	}

	@DeleteMapping("/employees/{id}")
	public ResponseEntity<Employee> deleteEmployee(@PathVariable Integer id)
	{
        log.warn("Deleting employee with ID: {}", id);
		Optional<Employee> optionalEmployee = employeeRepository.findById(id);

		if (optionalEmployee.isPresent())
		{
			Employee employee = optionalEmployee.get();
			employeeRepository.deleteById(id);
            log.info("Employee deleted successfully with ID: {}", id);
			return ResponseEntity.ok(employee);
		} else
		{
            log.error("Attempt to delete non-existing employee with ID: {}", id);
			return ResponseEntity.notFound().build();
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
		} else
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
			emailService.sendForgotPasMailLink(email, resetLink);

			return ResponseEntity.ok(Map.of("message", "Reset link sent to email"));
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Invalid token"));
	}

}
