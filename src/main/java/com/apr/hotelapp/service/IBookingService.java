package com.apr.hotelapp.service;

import java.util.List;

import com.apr.hotelapp.model.BookedRoom;

public interface IBookingService {

	String saveBooking(Long roomId, BookedRoom bookingRequest);

	BookedRoom findByBookingConfirmationCode(String confirationCode);

	List<BookedRoom> getAllBookings();

	void cancelBooking(Long bookingId);


}
