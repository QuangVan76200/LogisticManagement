package com.example.demo.jwt;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Role;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JWTUtil {

	private String secret = "OnlyMyheartInMyMemories";

	public String extractUsername(String token) {
		Claims claims = extractAllClaims(token);
		return claims.get("username").toString();
	}

	public Date extractExpiration(String token) {
		return extractClaims(token, Claims::getExpiration);
	}

	public <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	public Claims extractAllClaims(String token) {
		return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
	}

	private boolean isTokenExpirxed(String token) {
		return extractExpiration(token).before(new Date());
	}

	public String generateToken(String userName, Set<Role> roles) {
		Map<String, Object> claims = new HashMap<String, Object>();
		claims.put("username", userName);
		claims.put("roles", roles.stream().map(Role::getRoleName).collect(Collectors.toList()));
		return createToken(claims, userName);
	}

	private String createToken(Map<String, Object> claims, String subject) {
		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
				.signWith(SignatureAlgorithm.HS256, secret).compact();
	}

	public Boolean validateToken(String token, UserDetails details) {
		final String userName = extractUsername(token);
		return (userName.equals(details.getUsername()) && !isTokenExpirxed(token));
	}

}
