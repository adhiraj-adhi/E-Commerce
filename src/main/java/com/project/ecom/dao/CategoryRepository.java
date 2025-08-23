package com.project.ecom.dao;

import com.project.ecom.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

	Category findByCategoryNameIgnoreCase(String categoryName);
}
