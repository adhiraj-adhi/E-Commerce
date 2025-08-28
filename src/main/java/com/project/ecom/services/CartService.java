package com.project.ecom.services;

import com.project.ecom.payload.CartDTO;
import com.project.ecom.payload.CartItemDTO;

import java.util.List;

public interface CartService {
    CartDTO addProductToCart(Long productId, Integer quantity);

    List<CartDTO> getAllCartItems();

    CartDTO getUsersCartItems();

    CartDTO updateProductInCart(Long productId, String operation);

    CartItemDTO deleteFromCart(Long productId);
}
