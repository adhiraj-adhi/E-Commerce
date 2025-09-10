package com.project.ecom.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @NotBlank
    private String paymentMethod; // e.g., "Credit Card", "PayPal"
    private String paymentStatus; // e.g., "Completed", "Pending"
//    private Double amount;
    private String transactionId; // Unique ID from the payment gateway
    private String responseMessage; // Message from the payment gateway
    private String paymentGateway; // e.g., "Stripe", "PayPal"
//    private String paymentDate; // Store as String for simplicity, consider using LocalDateTime for real applications

    @OneToOne(mappedBy = "payment", cascade = {CascadeType.PERSIST ,CascadeType.MERGE})
    private Order order;
}
