package com.project.ecom.payload;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
	private Long productId;
	private String productName;
	private String image;
	@JsonIgnore // We don't want to expose the description in the API response
	private Integer quantity;
	private double price;
	private double discount;
	private double specialPrice;
}
