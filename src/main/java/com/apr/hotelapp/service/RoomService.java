package com.apr.hotelapp.service;

import java.io.IOException;
import java.math.BigDecimal;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.apr.hotelapp.exception.ResourceNotFoundException;
import com.apr.hotelapp.model.Room;
import com.apr.hotelapp.repository.RoomRepository;

import lombok.RequiredArgsConstructor;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RoomService implements IRoomService{

	private final RoomRepository roomRepository;
	
	@Override
	public Room addNewRoom(MultipartFile file, String roomType, BigDecimal roomPrice) throws SerialException, SQLException, IOException {
		Room room = new Room();
		room.setRoomType(roomType);
		room.setRoomPrice(roomPrice);
		if (!file.isEmpty()) {
			byte[] photoBytes = file.getBytes();
			Blob photoBlob = new SerialBlob(photoBytes);
			room.setPhoto(photoBlob);
		}
		return roomRepository.save(room);
	}

	@Override
	public List<String> getAllRoomTypes() {
		// TODO Auto-generated method stub
		return roomRepository.findDistinctRoomTypes();
	}

	@Override
	public List<Room> getAllRooms() {
		// TODO Auto-generated method stub
		return roomRepository.findAll();
	}

	@Override
	public byte[] getRoomPhotoByRoomId(Long roomId) throws SQLException {
		Optional<Room> theRoom = roomRepository.findById(roomId);
		if(theRoom.isEmpty()) {
			throw new ResourceNotFoundException("Room not found");
		}
		Blob photoBlob = theRoom.get().getPhoto();
		if(photoBlob != null) {
			return photoBlob.getBytes(1, (int) photoBlob.length());
		}
		return null;
	}

	@Override
	public void deleteRoom(Long id) {
		Optional<Room> theRoom = roomRepository.findById(id);
		if(theRoom.isPresent()) {
			roomRepository.deleteById(id);
		}
		
	}

}
