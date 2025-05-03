package com.apr.hotelapp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

import com.apr.hotelapp.exception.UserAlreadyExistsException;
import com.apr.hotelapp.model.User;
import com.apr.hotelapp.service.IUserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuthController {
	private final IUserService userService;
	
	@PostMapping("/register-user")
	public ResponseEntity<?> registerUser(User user){
		try {
			userService.registerUser(user);
			return ResponseEntity.ok("Registration successfull");
		} catch (UserAlreadyExistsException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		}
	}
}
