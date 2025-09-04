package com.ng.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
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

	public UserDetailsService userDetailsService()
	{
		return username -> repository.findByusername(username)
				.map(user -> org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
						.password(user.getPassword()).roles(user.getRoles()).build())
				.orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
	}

//	@Bean
//	public AuthenticationManager authManager(HttpSecurity http) throws Exception
//	{
//		return http.getSharedObject(AuthenticationManagerBuilder.class).userDetailsService(userDetailsService())
//				.passwordEncoder(null).and().build();
//	}

//	@Bean
//	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception
//	{
//		http.csrf(csrf -> csrf.disable())
//				.cors(cors -> cors.configurationSource(corsConfigurationSource())) // enable CORS
//				.authorizeHttpRequests(auth -> auth.anyRequest().authenticated()).httpBasic(httpBasic ->
//				{
//				});
//
//		return http.build();
//	}
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception
	{
		http.csrf(csrf -> csrf.disable()).cors(cors -> cors.configurationSource(corsConfigurationSource()))
				.authorizeHttpRequests(auth -> auth.requestMatchers("/api/v1/signup").permitAll() // public signup
						.requestMatchers("/api/v1/**").authenticated() // protect all other APIs
				).httpBasic(httpBasic ->
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

	// In-Memory Users with enceryption
//	@Bean
//	public UserDetailsService service(PasswordEncoder encoder)
//	{
//		UserDetails user1 = User.withUsername("Narsing").password(encoder.encode("1234")).roles("NORMAL").build();
//
//		UserDetails admin = User.withUsername("ADMIN").password(encoder.encode("1234")).roles("ADMIN").build();
//
//		return new InMemoryUserDetailsManager(user1, admin);
//	}

	@Bean
	public PasswordEncoder encoder()
	{
		return new BCryptPasswordEncoder();
	}

//	@Bean
//	public AuthenticationManager authManager(HttpSecurity http) throws Exception
//	{
//		return http.getSharedObject(AuthenticationManagerBuilder.class).userDetailsService(userDetailsService())
//				.passwordEncoder(encoder()) // <-- use your PasswordEncoder bean
//				.and().build();
//	}
	
	@Bean
	public AuthenticationManager authManager(HttpSecurity http) throws Exception
	{
		AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
		authBuilder.userDetailsService(userDetailsService()).passwordEncoder(encoder()); // use your PasswordEncoder
																							// bean
		return authBuilder.build(); // no .and() needed
	}

	// Security Rules
//	@Bean
//	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception
//	{
//		http.csrf(csrf -> csrf.disable()).cors(Customizer.withDefaults())
//				.authorizeHttpRequests(auth -> auth.anyRequest().authenticated()).httpBasic(Customizer.withDefaults());
//		return http.build();
//	}

	// Ignore static resources
//	@Bean
//	public WebSecurityCustomizer customizer()
//	{
//		return web -> web.ignoring().requestMatchers("/css/**", "/js/**", "/images/**");
//	}

	// In-Memory Users without enceryption
//	@Bean
//	public UserDetailsService service()
//	{
//		UserDetails user1 = User.withUsername("Narsing").password("{noop}1234").roles("NORMAL").build();
//		
//		UserDetails admin = User.withUsername("ADMIN").password("{noop}12345").roles("ADMIN").build();
//		
//		return new InMemoryUserDetailsManager(user1, admin);
//		
//	}

}
