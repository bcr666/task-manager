package io.github.bcr666.taskmanager.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.bcr666.taskmanager.security.JwtUtil;

@RestController
public class AuthenticationController {

	private AuthenticationManager authenticationManager;

	private JwtUtil jwtUtil;
	
	private UserDetailsService userDetailsService;
	
	public AuthenticationController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserDetailsService userDetailsService)
	{
		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
		this.userDetailsService = userDetailsService;
	}

	@PostMapping("/login")
	public Map<String, String> login(@RequestBody Map<String, String> user) {
		Authentication auth = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(user.get("username"), user.get("password")));
		String token = jwtUtil.generateToken(user.get("username"));
		return Map.of("token", token);
	}
	
	@PostMapping("/request-magic-link")
	public ResponseEntity<String> requestMagicLink(@RequestBody Map<String, String> payload) {
		String email = payload.get("email");

		// In production, you'd check if the user exists
		String token = jwtUtil.generateToken(email);

		// TODO: Email this instead. For now, just return it.
		String magicLink = "http://localhost:8080/magic-login?token=" + token;
		return ResponseEntity.ok(magicLink);
	}
	
	@GetMapping("/magic-login")
	public ResponseEntity<Map<String, String>> magicLogin(@RequestParam String token) {
		if (!jwtUtil.validateToken(token)) {
			return ResponseEntity.status(401).body(Map.of("error", "Invalid or expired token"));
		}

		String username = jwtUtil.extractUsername(token);
		UserDetails userDetails = userDetailsService.loadUserByUsername(username);
		UsernamePasswordAuthenticationToken auth =
			new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

		SecurityContextHolder.getContext().setAuthentication(auth);

		// Optionally: return a long-lived token for session use
		String sessionToken = jwtUtil.generateToken(username);
		return ResponseEntity.ok(Map.of("token", sessionToken));
	}
}
