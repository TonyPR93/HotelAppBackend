package com.apr.hotelapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.apr.hotelapp.model.Room;

public interface RoomRepository extends JpaRepository<Room, Long>{

}
