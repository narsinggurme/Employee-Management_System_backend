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

	@Transactional
	public void createOrUpdateSession(String username, String refreshToken)
	{
		Optional<UserSession> sessionOpt = userSessionRepository.findByUsername(username);
		UserSession session;
		if (sessionOpt.isPresent())
		{
			session = sessionOpt.get();
			session.setRefreshToken(refreshToken);
		} else
		{
			session = new UserSession();
			session.setUsername(username);
			session.setRefreshToken(refreshToken);
			session.setCreatedAt(LocalDateTime.now());
		}
		session.setLastActivity(LocalDateTime.now());
		userSessionRepository.save(session);
	}

	public boolean isSessionActive(String refreshToken, int inactivityMinutes)
	{
		Optional<UserSession> sessionOpt = userSessionRepository.findByRefreshToken(refreshToken);
		if (sessionOpt.isPresent())
		{
			UserSession session = sessionOpt.get();
			LocalDateTime now = LocalDateTime.now();
			if (session.getLastActivity().plusMinutes(inactivityMinutes).isAfter(now))
			{
				session.setLastActivity(now);
				userSessionRepository.save(session);
				return true;
			} else
			{
				userSessionRepository.delete(session);
				return false;
			}
		}
		return false;
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