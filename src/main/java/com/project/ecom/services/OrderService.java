package com.project.ecom.services;

import com.project.ecom.payload.OrderDTO;
import jakarta.transaction.Transactional;

public interface OrderService {
    @Transactional
    OrderDTO placeOrder(String username, Long addressId, String paymentMethod, String paymentGateway, String transactionId, String status, String responseMessage);
}
