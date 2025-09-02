package com.project.ecom.controllers;

import com.project.ecom.payload.CartDTO;
import com.project.ecom.payload.CartItemDTO;
import com.project.ecom.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CartController {
    @Autowired
    private CartService cartService;
    @PostMapping("/carts/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> addProductToCart(@PathVariable Long productId,
        @PathVariable Integer quantity) {
        CartDTO cartDTO = cartService.addProductToCart(productId, quantity);
        return new ResponseEntity<>(cartDTO, HttpStatus.CREATED);
    }

    @GetMapping("/carts")   // Admin can get all carts
    public ResponseEntity<List<CartDTO>> getAllCartItems() {
        List<CartDTO> cartDTOS = cartService.getAllCartItems();
        return new ResponseEntity<>(cartDTOS, HttpStatus.OK);
    }

    @GetMapping("/carts/users/cart")
    public ResponseEntity<CartDTO> getUsersCartItems() {
        CartDTO cartDTO = cartService.getUsersCartItems();
        return new ResponseEntity<>(cartDTO, HttpStatus.OK);
    }

    @PatchMapping("/carts/products/{productId}/quantity/{operation}")
    public ResponseEntity<CartDTO> updateProductInCart(@PathVariable Long productId,
                                                             @PathVariable String operation) {
        CartDTO cartDTO = cartService.updateProductInCart(productId, operation);
        return new ResponseEntity<>(cartDTO, HttpStatus.ACCEPTED);
    }

//    @DeleteMapping("/carts/{cartId}/product/{productId}")
    @DeleteMapping("/carts/product/{productId}")
    public ResponseEntity<CartItemDTO> deleteFromCart(@PathVariable Long productId) {
        CartItemDTO cartItemDTO = cartService.deleteFromCart(productId);
        return new ResponseEntity<>(cartItemDTO, HttpStatus.OK);
    }
}
