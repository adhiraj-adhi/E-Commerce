package com.project.ecom.services;

import com.project.ecom.dao.CategoryRepository;
import com.project.ecom.exceptions.APIException;
import com.project.ecom.exceptions.ResourceNotFoundException;
import com.project.ecom.model.Category;
import com.project.ecom.payload.CategoryDTO;
import com.project.ecom.payload.CategoryResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {
	private CategoryRepository catRepository;
	
	@Autowired
	private ModelMapper modelMapper;

	public CategoryServiceImpl(CategoryRepository catRepository) {
		this.catRepository = catRepository;
	}
	
	

	@Override
	public CategoryResponse getAllCategoriesService(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
		Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
				? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();
		Pageable pageable = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
		Page<Category> page = catRepository.findAll(pageable);
		
		List<Category> categories = page.getContent();
		if(categories.isEmpty())
			throw new APIException("There's no category present");
		
		/* Using without ModelMapper:
		 * CategoryDTO categoryDTO = new CategoryDTO();
		 * List<CategoryDTO> categoryDTOList = categories.stream().map(category -> {
		 * 	categoryDTO.setCategoryId(category.getCategoryId());
		 *  categoryDTO.setCategoryName(category.getCategoryName());
		 *	return categoryDTO;
		 *	}).toList();
		 *
		 * CategoryResponse categoryResponse = new CategoryResponse();
		 * categoryResponse.setContent(categoryDTOList);
		 * return categoryResponse;
		 */
		
		// With ModelMapper:
		
				
		List<CategoryDTO> categoryDTOs = categories.stream()
				.map(category -> modelMapper.map(category, CategoryDTO.class))
				.toList();
		
		CategoryResponse categoryResponse = new CategoryResponse();
		categoryResponse.setContent(categoryDTOs);
		categoryResponse.setPageNumber(pageNumber);
		categoryResponse.setPageSize(pageSize);
		categoryResponse.setTotalElements(page.getTotalElements());
		categoryResponse.setTotalPages(page.getTotalPages());
		categoryResponse.setLastPage(page.isLast());
		return categoryResponse;
	}

	@Override
//	public boolean createCategoryService(Category category) {
	public CategoryDTO createCategoryService(CategoryDTO categoryDTO) {
		Category category = modelMapper.map(categoryDTO, Category.class);
		// Before saving we need to check whether the category with same categoryName exists
		Category savedCategory = catRepository.findByCategoryNameIgnoreCase(category.getCategoryName());
		if (savedCategory == null) {
			try {
				catRepository.save(category);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			throw new APIException("Category with the name " + category.getCategoryName() + " already exists");
		}

		return modelMapper.map(category, CategoryDTO.class);
	}

//	@Override
//	public boolean deleteCategoryService(Long id) {
//		boolean status = false;
//		try {
//			status = categories.removeIf(category -> category.getCategoryId().equals(id));
//		} 
//		catch (Exception e) {
//			e.printStackTrace();
//		}
//		return status;
//	}

	@Override
	public CategoryDTO deleteCategoryService(Long id) {
		Optional<Category> optCategory = catRepository.findById(id);

		if (optCategory.isEmpty())
//			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource Not Found");
			throw new ResourceNotFoundException("Category", id, "categoryId");
		else {
			catRepository.deleteById(id);
		}
		return modelMapper.map(optCategory.get(), CategoryDTO.class);
	}

	@Override
	public CategoryDTO updateCategoryService(Long categoryId, CategoryDTO categoryDTO) {
		Category category = modelMapper.map(categoryDTO, Category.class);
		// Before updating we need to check whether the category with updated categoryName exists
		Category savedCategory = catRepository.findByCategoryNameIgnoreCase(category.getCategoryName());
		if (savedCategory != null) {
			throw new APIException("Category with the name " + category.getCategoryName() + " already exists");
		} else {
			Optional<Category> optionalCategory = catRepository.findById(categoryId);
			if (optionalCategory.isEmpty())
//				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource Not Found");
				throw new ResourceNotFoundException("Category", categoryId, "categoryId");

			Category oldCategory = optionalCategory.get();
			oldCategory.setCategoryName(category.getCategoryName());
			catRepository.save(oldCategory);
			return modelMapper.map(oldCategory, CategoryDTO.class);
		}
	}

}
