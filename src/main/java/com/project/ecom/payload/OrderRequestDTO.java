package com.project.ecom.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// This is going to represent the request format in which the front end application or clients like
//
//postman can send the request when the user places the new order.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDTO {
    private Long addressId;
    private String paymentMethod;
    private String paymentGateway;
    private String transactionId;
    private String status;
    private String responseMessage;
}
