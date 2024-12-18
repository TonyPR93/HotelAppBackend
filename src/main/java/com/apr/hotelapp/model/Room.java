package com.apr.hotelapp.model;

import java.math.BigDecimal;
import java.sql.Blob;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.text.RandomStringGenerator;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
public class Room {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String roomType;
	private BigDecimal roomPrice;
	private boolean isBooked = false;
	
	@Lob
	private Blob photo;
	
	@OneToMany(mappedBy="room", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<BookedRoom> bookings;
 
	public Room() {
		this.bookings = new ArrayList<>();
	}
	
	public void addBooking(BookedRoom booking) {
		if (bookings == null ) {
			bookings=new ArrayList<>();
		}
		bookings.add(booking);
		booking.setRoom(this);
		isBooked = true;
		
		RandomStringGenerator generator = new RandomStringGenerator.Builder()
			     .withinRange('a', 'z').get();
		String bookingCode = generator.generate(10);
		booking.setBookingConfirmationCode(bookingCode);
	}
	
}