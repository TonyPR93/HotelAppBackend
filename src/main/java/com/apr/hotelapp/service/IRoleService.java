package com.apr.hotelapp.service;

import java.util.List;

import com.apr.hotelapp.model.Role;
import com.apr.hotelapp.model.User;

public interface IRoleService {
	
	List<Role> getRoles();
	
	Role createRole(Role theRole);
	
	void deleteRole(Long id);
	
	Role findRoleByName(String name);
	
	User removeUserFromRole(Long userId, Long roleId);
	
	User assignRoleToUser(Long userId, Long roleId);
	
	Role removeAllUsersFromRole(Long roleId);

}
