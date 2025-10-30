# 💼 Employee Management System - Backend

This is the **backend service** for the Employee Management System, developed using **Spring Boot** and secured with **Spring Security**.  
It provides a complete set of **RESTful APIs** to manage employees, departments, and user accounts with **role-based authentication and authorization**.  

The system includes secure login, signup, and forgot password functionality using **real-time OTP verification via email**, ensuring strong security and seamless frontend integration.

---

## 🚀 Features

✅ **User Authentication & Security**
- Login and Signup with encrypted passwords (`BCryptPasswordEncoder`)
- Real-time OTP sent via email for signup and forgot password
- Role-based access control (Admin, Employee)
- Implemented using **Spring Security (Basic Authentication)**

✅ **Employee Management**
- Full CRUD (Create, Read, Update, Delete) operations for employees
- Department and role management with access restrictions
- Validations and centralized exception handling

✅ **Integration & Communication**
- Fully integrated with frontend
- Tested using **Postman**
- Clean JSON responses and status codes

✅ **Backend Architecture**
- Layered architecture: Controller → Service → Repository → Entity
- Hibernate ORM for seamless DB interaction
- Follows best coding and design practices

---

## 🧰 Tech Stack

| Component | Technology Used |
|------------|----------------|
| **Language** | Java 17+ |
| **Framework** | Spring Boot |
| **Security** | Spring Security (Basic Auth + OTP) |
| **Database** | MySQL (via Hibernate / JPA) |
| **Build Tool** | Maven |
| **Testing Tool** | Postman |
| **Email Service** | JavaMailSender (SMTP for OTP verification) |

---

## ⚙️ Getting Started

### 1️⃣ Clone the Repository
```bash
git clone https://github.com/narsinggurme/Employee-Management_System_backend.git
cd Employee-Management_System_backend
2️⃣ Configure the Database
Edit src/main/resources/application.properties:

properties
Copy code
spring.datasource.url=jdbc:mysql://localhost:3306/employee_db
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
3️⃣ Configure Email for OTP
Add the following properties for your email setup:

properties
Copy code
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=youremail@gmail.com
spring.mail.password=your_app_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
4️⃣ Run the Application
bash
Copy code
mvn spring-boot:run
or
Run the main class EmployeeManagementApplication.java from your IDE.

🔗 API Endpoints
Functionality	Method	Endpoint	Access
User Signup (with OTP)	POST	/api/auth/signup	Public
Verify OTP	POST	/api/auth/verify-otp	Public
User Login	POST	/api/auth/login	Public
Forgot Password	POST	/api/auth/forgot-password	Public
Reset Password	POST	/api/auth/reset-password	Public
Get All Employees	GET	/api/employees	Admin
Get Employee by ID	GET	/api/employees/{id}	Admin/Employee
Add New Employee	POST	/api/employees	Admin
Update Employee	PUT	/api/employees/{id}	Admin
Delete Employee	DELETE	/api/employees/{id}	Admin

🔒 Security Overview
Spring Security for authentication and authorization

Role-based access for Admin and Employee

Encrypted passwords using BCrypt

Real-time OTP verification for signup and password reset

Secure REST endpoints using Basic Authentication

🧪 Testing
All APIs have been tested using Postman with real credentials, OTPs, and secure headers.
Responses are structured in JSON with proper HTTP status codes and messages.

🖥️ Frontend Integration
This backend is fully integrated with the frontend application.
Features such as Login, Signup, OTP Verification, Forgot Password, and CRUD operations work seamlessly end-to-end.

👨‍💻 Author
Narsing Gurme
Developer – Spring Boot | Java | Full Stack
📧 Email: narsinggurme@gmail.com
🌐 GitHub: @narsinggurme

🏁 Conclusion
The Employee Management System Backend is a secure, scalable, and production-ready backend service built using Spring Boot, Spring Security, and Hibernate.
It supports real-time email OTP verification, encrypted authentication, and full employee management with role-based access — ideal for enterprise-grade applications.

📄 Repository Info
Short Description:

Backend service for Employee Management System with Spring Boot, Spring Security, OTP-based authentication, and MySQL integration.

Tags:
Spring Boot · Spring Security · REST API · OTP · Hibernate · MySQL · Employee Management · Java Backend