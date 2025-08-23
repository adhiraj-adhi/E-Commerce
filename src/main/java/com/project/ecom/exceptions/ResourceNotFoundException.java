package com.project.ecom.exceptions;

public class ResourceNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	String resourceName;
	Long fieldId;
	String field;
	String fieldName;
		
	public ResourceNotFoundException() {
		super();
	}
	
	public ResourceNotFoundException(String resourceName, Long fieldId, String field) {
		super(String.format("%s not found with %s: %d", resourceName, field, fieldId));
		this.resourceName = resourceName;
		this.fieldId = fieldId;
		this.field = field;
	}
	
	public ResourceNotFoundException(String resourceName, String fieldName, String field) {
		super(String.format("%s not found with %s: %s", resourceName, field, fieldName));
		this.resourceName = resourceName;
		this.field = field;
		this.fieldName = fieldName;
	}
}
