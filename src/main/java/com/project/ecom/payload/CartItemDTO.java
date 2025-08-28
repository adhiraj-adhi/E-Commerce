package com.project.ecom.payload;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {
    private Long cartItemId;
    @JsonIgnore  // just to ignore cart details in CartDTO response
    private CartDTO cart;
    private ProductDTO productDTO;
    private double specialPrice;
    private double discount;
    private Integer quantity;  // this is quantity added to cart but not the quantity of product in stock
}
