package com.project.ecom.exceptions;

public class APIException extends RuntimeException {
	/* This exception class will be designed to encapsulate errors related to API operations, 
	 * making it easier for us to handle and communicate specific issues that occur during the 
	 * runtime of the application.
	 * In other words, these errors are especially those that would be related to client requests 
	 * and data processing within the API.
	 * 
	 * Example: If we're trying to create a category that already exists, we should get the 
	 * error or if we're trying to update a category name with the name that already exists in 
	 * that scenario also we should get the error.
	 */
	
	
	private static final long serialVersionUID = 1L;
	public APIException() {}
	public APIException(String message) {
		super(message);
	}
}
