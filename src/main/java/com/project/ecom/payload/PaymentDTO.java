package com.project.ecom.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private Long paymentId;
    private String paymentMethod; // e.g., "Credit Card", "PayPal"
    private String transactionId;
    private String paymentStatus; // e.g., "Completed", "Pending"
    private String responseMessage;
    private  String paymentGateway; // e.g., "Stripe", "PayPal"
}
