package com.apr.hotelapp.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.apr.hotelapp.exception.PhotoRetrievalException;
import com.apr.hotelapp.exception.ResourceNotFoundException;
import com.apr.hotelapp.model.BookedRoom;
import com.apr.hotelapp.model.Room;
import com.apr.hotelapp.response.BookingResponse;
import com.apr.hotelapp.response.RoomResponse;
import com.apr.hotelapp.service.BookingService;
import com.apr.hotelapp.service.IRoomService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rooms")
public class RoomController {
	private final IRoomService roomService;
	private final BookingService bookingService;
	
	@PostMapping("/add/new-room")
	public ResponseEntity<RoomResponse> addNewRoom(
			@RequestParam("photo") MultipartFile photo, 
			@RequestParam("roomType") String roomType, 
			@RequestParam("roomPrice") BigDecimal roomPrice) throws SerialException, SQLException, IOException{
				Room savedRoom = roomService.addNewRoom(photo, roomType, roomPrice);
				RoomResponse response = new RoomResponse(savedRoom.getId(), savedRoom.getRoomType(), savedRoom.getRoomPrice());
				return ResponseEntity.ok(response);
				
	}
	
	@GetMapping("/room/types")
	public List<String> getRoomTypes(){
		return roomService.getAllRoomTypes();
	}
	
	@GetMapping("/all-rooms")
	public ResponseEntity<List<RoomResponse>> getAllRooms() throws SQLException{
		List<Room> rooms = roomService.getAllRooms();
		List<RoomResponse> roomResponses = new ArrayList<>();
		for (Room room : rooms) {
			byte[] photoBytes = roomService.getRoomPhotoByRoomId(room.getId());
			if(photoBytes != null && photoBytes.length > 0) {
				String base64Photo = Base64.getEncoder().encodeToString(photoBytes);
				RoomResponse roomResponse = getRoomResponse(room);
				roomResponse.setPhoto(base64Photo);
				roomResponses.add(roomResponse);
			}
		}
		return ResponseEntity.ok(roomResponses);
	}
	
	//PathVariable peut donner un alias
	@DeleteMapping("/delete/room/{roomId}")
	public ResponseEntity<Void> deleteRoom(@PathVariable("roomId") Long id){
		roomService.deleteRoom(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
    @PutMapping("/update/{roomId}")
    public ResponseEntity<RoomResponse> updateRoom(@PathVariable Long roomId,
                                                   @RequestParam(required = false)  String roomType,
                                                   @RequestParam(required = false) BigDecimal roomPrice,
                                                   @RequestParam(required = false) MultipartFile photo) throws SQLException, IOException {
        byte[] photoBytes = photo != null && !photo.isEmpty() ?
                photo.getBytes() : roomService.getRoomPhotoByRoomId(roomId);
        Blob photoBlob = photoBytes != null && photoBytes.length >0 ? new SerialBlob(photoBytes): null;
        Room theRoom = roomService.updateRoom(roomId, roomType, roomPrice, photoBytes);
        theRoom.setPhoto(photoBlob);
        RoomResponse roomResponse = getRoomResponse(theRoom);
        return ResponseEntity.ok(roomResponse);
    }
    
    @GetMapping("/room/{roomId}")
    public ResponseEntity<Optional<RoomResponse>> getRoomById(@PathVariable Long roomId){
    	Optional<Room> theRoom = roomService.getRoomById(roomId);
    	
    	return theRoom.map(room ->{
    		RoomResponse roomResponse = getRoomResponse(room);
    		return ResponseEntity.ok(Optional.of(roomResponse));
    	}).orElseThrow(() -> new ResourceNotFoundException("Room not found"));
    }
    
    @GetMapping("/available-rooms")
    public ResponseEntity<List<RoomResponse>> getAvailableRooms(
            @RequestParam("checkInDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate checkInDate,
            @RequestParam("checkOutDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate checkOutDate,
            @RequestParam("roomType") String roomType) throws SQLException {
    	System.out.println("Received request: " + checkInDate + " to " + checkOutDate + ", type=" + roomType);
        List<Room> availableRooms = roomService.getAvailableRooms(checkInDate, checkOutDate, roomType);
        List<RoomResponse> roomResponses = new ArrayList<>();
        for (Room room : availableRooms){
            byte[] photoBytes = roomService.getRoomPhotoByRoomId(room.getId());
            if (photoBytes != null && photoBytes.length > 0){
            	String photoBase64 = Base64.getEncoder().encodeToString(photoBytes);
                RoomResponse roomResponse = getRoomResponse(room);
                roomResponse.setPhoto(photoBase64);
                roomResponses.add(roomResponse);
            }
        }
        if(roomResponses.isEmpty()){
            return ResponseEntity.noContent().build();
        }else{
            return ResponseEntity.ok(roomResponses);
        }
    }
	private RoomResponse getRoomResponse(Room room) {
		List<BookedRoom> bookings = getAllBookingsByRoomId(room.getId());
		/*List<BookingResponse> bookingInfo = bookings
				.stream()
				.map(booking -> new BookingResponse(booking.getBookingId(), booking.getCheckInDate(), booking.getCheckOutDate(), booking.getBookingConfirmationCode())).toList();*/
		byte[] photoBytes = null;
		Blob photoBlob = room.getPhoto();
		if(photoBlob != null) {
			try {
				photoBytes = photoBlob.getBytes(1, (int) photoBlob.length());
			} catch (SQLException e) {
				throw new PhotoRetrievalException("Error retreving photo");
			}
		}
		return new RoomResponse(room.getId(), room.getRoomType(), room.getRoomPrice(), room.isBooked(), photoBytes);
	}

	private List<BookedRoom> getAllBookingsByRoomId(Long roomId) {
		return bookingService.getAllBookingsByRoomId(roomId);
	}
}
