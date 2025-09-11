package com.ng.controller;

import com.ng.config.JwtUtil;
import com.ng.entity.Employee;
import com.ng.entity.MyUser;
import com.ng.exception.ResourceNotFound;
import com.ng.repository.EmployeeRepository;
import com.ng.repository.UserRepository;
import com.ng.service.UserSessionService;

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

	@PostMapping("/login")
	public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials)
	{
		String username = credentials.get("username");
		String password = credentials.get("password");

		Optional<MyUser> userOpt = userRepository.findByusername(username);
		if (userOpt.isPresent())
		{
			MyUser user = userOpt.get();
			if (passwordEncoder.matches(password, user.getPassword()))
			{
				String accessToken = jwtUtil.generateAccessToken(username);
				String refreshToken = jwtUtil.generateRefreshToken(username);
				boolean created = sessionService.createNewSession(username, refreshToken);

				if (!created)
				{
					return ResponseEntity.status(HttpStatus.CONFLICT) // 409 Conflict
							.body(Map.of("message", "User already logged in from another device"));
				}

				Map<String, Object> response = new HashMap<>();
				response.put("accessToken", accessToken);
				response.put("refreshToken", refreshToken);
				response.put("username", user.getUsername());
				response.put("roles", user.getRoles());
				response.put("expiresIn", jwtUtil.getExpiration(accessToken));
				return ResponseEntity.ok(response);
			}
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid credentials"));
	}

	@PostMapping("/refresh")
	public ResponseEntity<Map<String, Object>> refresh(@RequestBody Map<String, String> request)
	{
		String refreshToken = request.get("refreshToken");

		try
		{
			if (!sessionService.validateRefreshToken(refreshToken))
			{
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body(Map.of("message", "Invalid session, please login again"));
			}

			String username = jwtUtil.extractUsername(refreshToken);

			if (!jwtUtil.isTokenExpired(refreshToken))
			{
				String newAccessToken = jwtUtil.generateAccessToken(username);
				String newRefreshToken = jwtUtil.generateRefreshToken(username);

				sessionService.createNewSession(username, newRefreshToken);

				return ResponseEntity.ok(Map.of("accessToken", newAccessToken, "refreshToken", newRefreshToken));
			}
			else
			{
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body(Map.of("message", "Refresh token expired, please login again"));
			}

		} catch (Exception e)
		{
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid refresh token"));
		}
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
		Employee saveEmployee = employeeRepository.save(employee);
		return new ResponseEntity<>(saveEmployee, HttpStatus.CREATED);
	}

	@GetMapping("/employees")
	public List<Employee> gelAllEmployee()
	{
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
		Optional<Employee> optionalEmployee = employeeRepository.findById(id);

		if (optionalEmployee.isPresent())
		{
			Employee employee = optionalEmployee.get();
			employeeRepository.deleteById(id);
			return ResponseEntity.ok(employee);
		} else
		{
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
			return ResponseEntity.ok(response);
		} else
		{
			response.put("message", "username is not found");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

		}
	}

	@PostMapping("/forgot-password/reset")
	public ResponseEntity<Map<String, String>> resetPassword(@RequestBody Map<String, String> request)
	{
		String username = request.get("username");
		String newPassword = request.get("password");
		Map<String, String> response = new HashMap<String, String>();
		Optional<MyUser> userOpt = userRepository.findByusername(username);
		if (userOpt.isPresent())
		{
			MyUser user = userOpt.get();
			user.setPassword(passwordEncoder.encode(newPassword));
			userRepository.save(user);
			response.put("message", "Password updated successfully");
			return ResponseEntity.ok(response);
		} else
		{
			response.put("message", "username is not found");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}
	}

}
