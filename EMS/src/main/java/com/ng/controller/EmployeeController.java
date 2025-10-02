package com.ng.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ng.entity.Employee;
import com.ng.exception.ResourceNotFound;
import com.ng.repository.EmployeeRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/")
@CrossOrigin(origins = "http://localhost:4200")
public class EmployeeController
{
	private static final Logger log = LoggerFactory.getLogger(EmployeeController.class);

	@Autowired
	private EmployeeRepository employeeRepository;

	@PostMapping("/empl")
	public ResponseEntity<Object> createEmployee(@RequestBody Employee employee)
	{
		log.info("Creating new employee");

		String email = employee.getEmail();
		String phone = employee.getPhone();

		if (employeeRepository.existsByEmail(email))
		{
			return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("status", "error", "message",
					"Email already exist. Please use another email.", "email", email));
		}

		if (employeeRepository.existsByPhone(phone))
		{
			return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("status", "error", "message",
					"Phone already exists. Please use another phone", "phone", phone));
		}
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
		employee.setDesignation(employeeDetails.getDesignation());
		employee.setAge(employeeDetails.getAge());
		employee.setSalary(employeeDetails.getSalary());
		employee.setJoiningDate(employeeDetails.getJoiningDate());

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

}
