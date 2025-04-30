package com.apr.hotelapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.apr.hotelapp.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long>{

	Optional<Role> findByName(String role);

}
