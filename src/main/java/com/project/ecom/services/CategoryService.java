package com.project.ecom.services;

import com.project.ecom.payload.CategoryDTO;
import com.project.ecom.payload.CategoryResponse;

public interface CategoryService {
	CategoryResponse getAllCategoriesService(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
	CategoryDTO createCategoryService(CategoryDTO categoryDTO);
	CategoryDTO deleteCategoryService(Long id);
	CategoryDTO updateCategoryService(Long categoryId, CategoryDTO categoryDTO);
}