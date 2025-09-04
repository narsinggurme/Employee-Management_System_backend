package com.ng.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ng.entity.Employee;
import com.ng.repository.EmployeeRepository;


@Service
public class EmployeeService
{
	@Autowired
	private EmployeeRepository employeeRepository;
	
	public Employee postEmployee(Employee employee)
	
	{
		return employeeRepository.save(employee);
	}
	public List<Employee> getAllEmployee()
	{
		return employeeRepository.findAll();
	}
	
}
