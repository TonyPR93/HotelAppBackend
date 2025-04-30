package com.apr.hotelapp.service;

import java.util.List;

import com.apr.hotelapp.model.User;

public interface IUserService {
	User registerUser(User user);
	
	List<User> getUsers();
	
	void deleteUser(String email);
	
	User getUser(String email);
}
