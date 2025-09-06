package com.ng.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.ng.repository.UserRepository;

@Configuration
@EnableWebSecurity
public class MySecirutyConfig
{
	@Autowired
	private UserRepository repository;

	@Bean
	public UserDetailsService userDetailsService()
	{
		return username -> repository.findByusername(username).map(user ->
		{
			System.out.println(">>> Username from DB: " + user.getUsername());

			return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
					.password(user.getPassword()).roles(user.getRoles()).build();
		}).orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception
	{
		http.csrf(csrf -> csrf.disable()).cors(cors -> cors.configurationSource(corsConfigurationSource()))
				.authorizeHttpRequests(auth -> auth.requestMatchers("/api/v1/signup", 
						"/api/v1/login", "/api/v1/forgot-password/check-username", "/api/v1/forgot-password/reset").permitAll()
						.requestMatchers("/api/v1/employees").hasRole("ADMIN").requestMatchers("/api/v1/employees/**")
						.authenticated())
				.httpBasic(httpBasic ->
				{
				});
		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource()
	{
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(List.of("http://localhost:4200"));
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(List.of("*"));
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Bean
	public PasswordEncoder encoder()
	{
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authManager(HttpSecurity http) throws Exception
	{
		AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
		authBuilder.userDetailsService(userDetailsService()).passwordEncoder(encoder());
		return authBuilder.build();
	}

}
