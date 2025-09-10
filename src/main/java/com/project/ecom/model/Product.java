package com.project.ecom.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
	@Id
//	@GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long productId;
	@NotBlank
	@Size(min = 3, message = "Product name must contain at least 3 characters")
	private String productName;
	private String image;
	@NotBlank
	@Size(min = 6, message = "Product description must contain at least 6 characters")
	private String description;
	@JsonIgnore  // We don't want to expose the quantity of product to the client side
	private Integer quantity;
	private double price;
	private double discount;
	private double specialPrice;
	
	@ManyToOne
	@JoinColumn(name = "category_id")
	private Category category;

	// One Product can be associated with only one seller:
	@ManyToOne
	@JoinColumn(name = "seller_id")
	private User seller;

	// Many product can be associated with one cart:
	@OneToMany(mappedBy = "product", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER, orphanRemoval = true)
	private List<CartItem> products = new ArrayList<>();
}
