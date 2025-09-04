//package com.ng.Employee.controller;
//
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//import com.ng.Employee.entity.MyUser;
//import com.ng.Employee.repository.UserRepository;
//
//@Component
//public class DataInitializer {
//
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
//        this.userRepository = userRepository;
//        this.passwordEncoder = passwordEncoder;
//    }
//
//    @Bean
//    public CommandLineRunner initUsers() {
//        return args -> {
//            if (userRepository.findByusername("Narsing").isEmpty()) {
//                MyUser user = new MyUser();
//                user.setUsername("Narsing");
//                user.setPassword(passwordEncoder.encode("1234")); // encode password
//                user.setRoles("NORMAL");
//                userRepository.save(user);
//            }
//        };
//    }
//}



