package com.apr.hotelapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.apr.hotelapp.model.BookedRoom;

public interface BookingRepository extends JpaRepository<BookedRoom, Long>{

	List<BookedRoom> findByRoomId(Long roomId);

	BookedRoom findByBookingConfirmationCode(String confirmationCode);


}
