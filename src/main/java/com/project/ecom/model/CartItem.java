package com.project.ecom.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cart_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartItemId;

    // Many cart items -> 1 cart
    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    // 1 cart item can have same product multiple times for different users
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private double productPrice;

    private double discount;

    private Integer quantity;
}
