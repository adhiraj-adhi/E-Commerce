package com.project.ecom.exceptions;

import com.project.ecom.payload.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> methodArgumentNotValidException(MethodArgumentNotValidException e) {
		Map<String, String> response = new HashMap<>();
		
		e.getBindingResult().getAllErrors().forEach(err -> {
			String fieldName = ((FieldError)err).getField();
			String message = err.getDefaultMessage();
			response.put(fieldName, message);
		});
		
		return new ResponseEntity<Map<String,String>>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(ResourceNotFoundException.class)
//	public ResponseEntity<String> resourceNotFoundException(ResourceNotFoundException re) {
	public ResponseEntity<APIResponse> resourceNotFoundException(ResourceNotFoundException re) {
		APIResponse apiResponse = new APIResponse(re.getMessage(), false);
		return new ResponseEntity<APIResponse>(apiResponse, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(APIException.class)
//	public ResponseEntity<String> apiException(APIException ae) {
	public ResponseEntity<APIResponse> apiException(APIException ae) {
		return new ResponseEntity<APIResponse>(new APIResponse(ae.getMessage(), false), HttpStatus.BAD_REQUEST);
	}
}
