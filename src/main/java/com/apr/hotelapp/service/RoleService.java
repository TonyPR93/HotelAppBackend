package com.apr.hotelapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.apr.hotelapp.exception.RoleAlreadyExistsException;
import com.apr.hotelapp.exception.UserAlreadyExistsException;
import com.apr.hotelapp.model.Role;
import com.apr.hotelapp.model.User;
import com.apr.hotelapp.repository.RoleRepository;
import com.apr.hotelapp.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RoleService implements IRoleService{

	private final RoleRepository roleRepository;
	private final UserRepository userRepository;

	@Override
	public List<Role> getRoles() {
		return roleRepository.findAll();
	}

	@Override
	public Role createRole(Role theRole) {
		String roleName = "ROLE_"+theRole.getName().toUpperCase();
		Role role = new Role(roleName);
		if(roleRepository.existsByName(role)) {
			throw new RoleAlreadyExistsException(role.getName() + " already exists");
		}
		return roleRepository.save(role);
	}

	@Override
	public void deleteRole(Long roleId) {
		this.removeAllUsersFromRole(roleId);
		roleRepository.deleteById(roleId);
	}

	@Override
	public Role findRoleByName(String name) {
		return roleRepository.findByName(name).get();
	}


	@Override
	public User removeUserFromRole(Long userId, Long roleId) {
		Optional<User> user = userRepository.findById(userId);
		Optional<Role> role = roleRepository.findById(roleId);
		if( role.isPresent() && role.get().getUsers().contains(user.get())) {
			role.get().removeUserFromRole(user.get());
			roleRepository.save(role.get());
			return user.get();
		}
		throw new UsernameNotFoundException("user not found");
		
	}

	@Override
	public User assignRoleToUser(Long userId, Long roleId) {
		Optional<User> user = userRepository.findById(userId);
		Optional<Role> role = roleRepository.findById(roleId);
		if (role.isPresent() && user.get().getRoles().contains(role.get())) {
			throw new UserAlreadyExistsException(user.get().getFirstName() + " is already assing to the " + role.get().getName()+ " role" );
		} 
		if (role.isPresent()) {
			role.get().assingRoleToUser(user.get());
		}
		roleRepository.save(role.get());
		return user.get();
	}

	@Override
	public Role removeAllUsersFromRole(Long roleId) {
		Optional<Role> role = roleRepository.findById(roleId);
		role.get().removeAllUsersFromRole();
		return roleRepository.save(role.get());
	}

}
