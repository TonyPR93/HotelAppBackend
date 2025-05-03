package com.apr.hotelapp.exception;

public class RoleAlreadyExistsException  extends RuntimeException{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RoleAlreadyExistsException(String message) {
		super(message);
	}

}
