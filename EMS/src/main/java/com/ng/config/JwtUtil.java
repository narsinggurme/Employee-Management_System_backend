package com.ng.config;

import java.security.Key;
import java.util.Date;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil
{
	private final String SECRET_KEY = "NarsingSuperSecretKeyForJwtMustBeAtLeast32Bytes!";
	private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

	public String generateToken(String username)
	{
		return Jwts.builder().setSubject(username).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) 
				.signWith(key, SignatureAlgorithm.HS256).compact();
	}

	public String extractUsername(String token)
	{
		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
				.getBody().getSubject();
	}

	public boolean validateToken(String token, UserDetails userDetails) {
	    final String username = extractUsername(token);
	    return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}


	private boolean isTokenExpired(String token)
	{
		Date exp = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
				.getBody().getExpiration();
		return exp.before(new Date());
	}

	public Date getExpiration(String token)
	{
		Claims claims = Jwts.parserBuilder().setSigningKey(SECRET_KEY.getBytes()).build().parseClaimsJws(token).getBody();
		return claims.getExpiration();
	}
}
