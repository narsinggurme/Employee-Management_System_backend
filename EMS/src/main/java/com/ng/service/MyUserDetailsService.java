package com.ng.service;

import com.ng.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService
{

	@Autowired
	private UserRepository repository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
	{
		return repository
				.findByusername(username).map(user -> User.withUsername(user.getUsername()).password(user.getPassword())
						.roles(user.getRoles()).build())
				.orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
	}
}
