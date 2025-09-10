package com.project.ecom.controllers;

import com.project.ecom.model.User;
import com.project.ecom.payload.OrderDTO;
import com.project.ecom.payload.OrderRequestDTO;
import com.project.ecom.services.OrderService;
import com.project.ecom.util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private AuthUtil authUtil;

//    @PostMapping("/order/users/payment/{paymentMethod}")
//    public ResponseEntity<OrderDTO> placeOrder(@PathVariable String paymentMethod, @RequestBody OrderRequestDTO orderRequestDTO) {
    @PostMapping("/order/users/payment")
    public ResponseEntity<OrderDTO> placeOrder(@RequestBody OrderRequestDTO orderRequestDTO) {
        // Implementation for placing an order
        User loggedInUser = authUtil.loggedInUser();
         OrderDTO orderDTO = orderService.placeOrder(
                loggedInUser.getUsername(),
                orderRequestDTO.getAddressId(),
                orderRequestDTO.getPaymentMethod(),
                orderRequestDTO.getPaymentGateway(),
                orderRequestDTO.getTransactionId(),
                orderRequestDTO.getStatus(),
                orderRequestDTO.getResponseMessage()
        );

         return new ResponseEntity<>(orderDTO, HttpStatus.CREATED);
    }
}
