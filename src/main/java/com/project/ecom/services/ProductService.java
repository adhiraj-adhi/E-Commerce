package com.project.ecom.services;

import com.project.ecom.model.Product;
import com.project.ecom.payload.APIResponse;
import com.project.ecom.payload.ProductDTO;
import com.project.ecom.payload.ProductResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {

	ProductDTO addProductService(Long categoryId, Product product);

	ProductResponse getAllProductsService(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

	ProductResponse getProductsByCategoryService(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

	ProductResponse getProductByKeywordService(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

	ProductDTO updateProductService(Long productId, Product product);

	ProductDTO deleteProductService(Long productId);

	APIResponse getProductsCountService();

	ProductDTO updateProductImageService(Long productId, MultipartFile productImage);

}
