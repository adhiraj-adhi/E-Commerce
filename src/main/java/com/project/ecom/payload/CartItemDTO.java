package com.project.ecom.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {
    private Long cartItemId;
    private CartDTO cart;
    private ProductDTO productDTO;
    private double productPrice;
    private double discount;
    private Integer quantity;
}
