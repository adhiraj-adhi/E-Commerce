package com.project.ecom.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class APIResponse {
	/*
	 * 1. Our project will grow in future and we have a GlobalExceptionHandler defined where we are 
	 * sending in a response.
	 * 2. Now the thing is if there was a ResourceNotFoundException or an ApiException we were sending 
	 * in String as the response. It is good but there is no structure that we have added over here and 
	 * its important that we add some sort of structure and for that we define a response class (APIResponse) 
	 * and then make use of it inside the GlobalExceptionHandler.
	 * 3. This standardizes the response where we are making use of APIResponse and whatever exceptions we 
	 * add in future we can make use of this class to return a standard structure.
	 * */
	private String message;
	private boolean status;
}
