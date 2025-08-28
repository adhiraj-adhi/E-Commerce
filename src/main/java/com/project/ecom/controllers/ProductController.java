package com.project.ecom.controllers;

import com.project.ecom.config.AppConstants;
import com.project.ecom.model.Product;
import com.project.ecom.payload.APIResponse;
import com.project.ecom.payload.ProductDTO;
import com.project.ecom.payload.ProductResponse;
import com.project.ecom.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class ProductController {
	@Autowired
	private ProductService productService;
	
	// Add a product
	@PostMapping("/categories/{categoryId}/product")
	public ResponseEntity<ProductDTO> addProduct(@PathVariable Long categoryId,
												 @Valid @RequestBody Product product
			) {
		ProductDTO productDTO = productService.addProductService(categoryId, product);
		return new ResponseEntity<>(productDTO, HttpStatus.CREATED);
	}
	
	// Get all products
	@GetMapping("/public/products")
	public ResponseEntity<ProductResponse> getAllProducts(
			@RequestParam(name="pageNumber", defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber,
			@RequestParam(name="pageSize", defaultValue = AppConstants.PAGE_SIZE) Integer pageSize,
			@RequestParam(name="sortBy", defaultValue = AppConstants.SORT_PRODUCT_BY) String sortBy,
			@RequestParam(name="sortOrder", defaultValue = AppConstants.SORT_ORDER) String sortOrder) {
		return ResponseEntity.ok(productService.getAllProductsService(pageNumber, pageSize, sortBy, sortOrder));
	}
	
	// Get all products of a category
	@GetMapping("public/categories/{categoryId}/products")
	public ResponseEntity<ProductResponse> getProductsByCategory(@PathVariable Long categoryId,
			@RequestParam(name="pageNumber", defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber,
			@RequestParam(name="pageSize", defaultValue = AppConstants.PAGE_SIZE) Integer pageSize,
			@RequestParam(name="sortBy", defaultValue = AppConstants.SORT_PRODUCT_BY) String sortBy,
			@RequestParam(name="sortOrder", defaultValue = AppConstants.SORT_ORDER) String sortOrder){
		return ResponseEntity.ok(productService.getProductsByCategoryService(categoryId, pageNumber, pageSize, sortBy, sortOrder));
	}
	
	// Get all products with a keyword
	@GetMapping("/public/products/keyword/{keyword}")
	public ResponseEntity<ProductResponse> getProductByKeyword(@PathVariable String keyword,
			@RequestParam(name="pageNumber", defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber,
			@RequestParam(name="pageSize", defaultValue = AppConstants.PAGE_SIZE) Integer pageSize,
			@RequestParam(name="sortBy", defaultValue = AppConstants.SORT_PRODUCT_BY) String sortBy,
			@RequestParam(name="sortOrder", defaultValue = AppConstants.SORT_ORDER) String sortOrder) {
		return new ResponseEntity<ProductResponse>(productService.getProductByKeywordService(keyword, pageNumber, pageSize, sortBy, sortOrder), HttpStatus.FOUND);
	}
	
	
	// Update a product (except image)
	@PutMapping("/products/{productId}")
	public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long productId, @Valid @RequestBody Product product) {
		return ResponseEntity.ok(productService.updateProductService(productId, product));
	}	
	
	
	// Delete a product
	@DeleteMapping("/products/{productId}")
	public ResponseEntity<ProductDTO> deleteProduct(@PathVariable Long productId) {
		return ResponseEntity.ok(productService.deleteProductService(productId));
	}
	
	
	// Update a product image
	@PutMapping("/admin/products/{productId}/image")
	public ResponseEntity<ProductDTO> updateProductImage(
			@PathVariable Long productId,
			@RequestParam("Image") MultipartFile productImage) {
		ProductDTO updatedProduct = productService.updateProductImageService(productId, productImage);
		return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
	}
	
	
	// Get Products By Seller
	
	
	
	
	// Get Product Count
//	@GetMapping("/admin/products/count")
//	public ResponseEntity<Long> getProductsCount() {
//		return ResponseEntity.ok(productService.getProductsCountService());
//	}
	@GetMapping("/admin/products/count")
	public ResponseEntity<APIResponse> getProductsCount() {
		return ResponseEntity.ok(productService.getProductsCountService());
	}
}
