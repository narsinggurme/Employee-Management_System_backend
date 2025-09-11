package com.ng.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ng.entity.UserSession;
import com.ng.repository.UserSessionRepository;

import jakarta.transaction.Transactional;

@Service
public class UserSessionService
{
	@Autowired
	private UserSessionRepository userSessionRepository;

//	public void createOrUpdateSession(String username, String refreshToken)
//	{
//		userSessionRepository.deleteByUsername(username);
//
//		UserSession session = new UserSession();
//		session.setUsername(username);
//		session.setRefreshToken(refreshToken);
//		session.setCreatedAt(LocalDateTime.now());
//
//		userSessionRepository.save(session);
//	}
//	
	  @Transactional
	    public boolean createNewSession(String username, String refreshToken) {
	        Optional<UserSession> existing = userSessionRepository.findByUsername(username);

	        if (existing.isPresent()) {
	            // User already logged in
	            return false;
	        }

	        UserSession session = new UserSession();
	        session.setUsername(username);
	        session.setRefreshToken(refreshToken);
	        session.setCreatedAt(LocalDateTime.now());
	        userSessionRepository.save(session);

	        return true;
	    }

	public boolean validateRefreshToken(String refreshToken)
	{
		return userSessionRepository.findByRefreshToken(refreshToken).isPresent();
	}

	@Transactional
	public void removeSession(String username)
	{
		userSessionRepository.deleteByUsername(username);
	}
}