package com.project.ecom.dao;

import com.project.ecom.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Long> {

	Page<Product> findByCategoryCategoryId(Long categoryId, Pageable pageable);

	Page<Product> findByProductNameIgnoreCaseContaining(String keyword, Pageable pageable);
	@Query("SELECT p FROM Product p WHERE p.seller.userId=?1 AND p.productId=?2")
    Product findBySellerIdAndProductId(Long userId, Long productId);
}
