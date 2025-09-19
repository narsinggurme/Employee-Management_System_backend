package com.ng.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tbl_employee")
public class Employee
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String name;
	private String email;
	private String phone;
	private String dept;
	private String designation;
	private BigDecimal salary;
	private int age;
	private LocalDate  joiningDate;
	
	public int getId()
	{
		return id;
	}
	public void setId(int id)
	{
		this.id = id;
	}
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public String getEmail()
	{
		return email;
	}
	public void setEmail(String email)
	{
		this.email = email;
	}
	public String getPhone()
	{
		return phone;
	}
	public void setPhone(String phone)
	{
		this.phone = phone;
	}
	public String getDept()
	{
		return dept;
	}
	public void setDept(String dept)
	{
		this.dept = dept;
	}
	public String getDesignation()
	{
		return designation;
	}
	public void setDesignation(String designation)
	{
		this.designation = designation;
	}
	public BigDecimal getSalary()
	{
		return salary;
	}
	public void setSalary(BigDecimal salary)
	{
		this.salary = salary;
	}
	public int getAge()
	{
		return age;
	}
	public void setAge(int age)
	{
		this.age = age;
	}
	public LocalDate  getJoiningDate()
	{
		return joiningDate;
	}
	public void setJoiningDate(LocalDate  joiningDate)
	{
		this.joiningDate = joiningDate;
	}
	
	public Employee(int id, String name, String email, String phone, String dept, String designation, BigDecimal salary,
			int age, LocalDate joiningDate)
	{
		super();
		this.id = id;
		this.name = name;
		this.email = email;
		this.phone = phone;
		this.dept = dept;
		this.designation = designation;
		this.salary = salary;
		this.age = age;
		this.joiningDate = joiningDate;
	}
	public  Employee()
	{
		super();
	}
}
