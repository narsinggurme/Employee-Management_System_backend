package com.ng.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ng.entity.UserSession;

public interface UserSessionRepository extends JpaRepository<UserSession, Long>
{
	Optional<UserSession> findByUsername(String username);
	Optional<UserSession> findByRefreshToken(String refreshToken);
	void deleteByUsername(String username);
}
