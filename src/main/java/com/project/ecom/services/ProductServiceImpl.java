package com.project.ecom.services;

import com.project.ecom.dao.CategoryRepository;
import com.project.ecom.dao.ProductRepository;
import com.project.ecom.exceptions.APIException;
import com.project.ecom.exceptions.ResourceNotFoundException;
import com.project.ecom.model.Category;
import com.project.ecom.model.Product;
import com.project.ecom.payload.APIResponse;
import com.project.ecom.payload.ProductDTO;
import com.project.ecom.payload.ProductResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private FileService fileService;
	
	@Value("${project.image}")
	private String path; // reading path from application.properties file

	@Override
	public ProductDTO addProductService(Long categoryId, Product product) {
		Category category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("Category", categoryId, "categoryId"));
		
		
		// Here, first we need to check if product is already present or not in the specified Category
		boolean isProductPresent = false;
		List<Product> products = category.getProducts();
		for(Product pro: products) {
			if(pro.getProductName().equals(product.getProductName())) {
				isProductPresent = true;
				break;
			}
		}
		
		if(isProductPresent)
			throw new APIException("Product already exists!!");
		
		product.setCategory(category);
		product.setImage("default.png");
		double specialPrice = product.getPrice() - (product.getPrice() * product.getDiscount() * 0.01);
		product.setSpecialPrice(specialPrice);

		Product savedProduct = productRepository.save(product);

		return modelMapper.map(savedProduct, ProductDTO.class);
	}

	@Override
	public ProductResponse getAllProductsService(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
		Sort sortAndOrder = sortOrder.equalsIgnoreCase("asc") 
				? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();
		Pageable pageable = PageRequest.of(pageNumber, pageSize, sortAndOrder);
		Page<Product> page = productRepository.findAll(pageable);
		
		List<Product> products = page.getContent();
		
		// Here, we need to check whether there is any product in database or not
		if(products.isEmpty())
			throw new APIException("No product exists!!");

		List<ProductDTO> productDTOs = products.stream().map(product -> modelMapper.map(product, ProductDTO.class))
				.toList();

		ProductResponse productResponse = new ProductResponse();
		productResponse.setContent(productDTOs);
		productResponse.setPageNumber(pageNumber);
		productResponse.setPageSize(pageSize);
		productResponse.setTotalElements(page.getTotalElements());
		productResponse.setTotalPages(page.getTotalPages());
		productResponse.setLastPage(page.isLast());

		return productResponse;
	}

	@Override
	public ProductResponse getProductsByCategoryService(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
		Category category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("Category", categoryId, "categoryId"));

		Sort sortAndOrder = sortOrder.equalsIgnoreCase("asc") 
				? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();
		Pageable pageable = PageRequest.of(pageNumber, pageSize, sortAndOrder);
		Page<Product> page = productRepository.findByCategoryCategoryId(categoryId, pageable);
		
		List<Product> products = page.getContent();

		List<ProductDTO> productDTOs = products.stream().map(product -> modelMapper.map(product, ProductDTO.class))
				.toList();

		ProductResponse productResponse = new ProductResponse();
		productResponse.setContent(productDTOs);
		productResponse.setPageNumber(pageNumber);
		productResponse.setPageSize(pageSize);
		productResponse.setTotalElements(page.getTotalElements());
		productResponse.setTotalPages(page.getTotalPages());
		productResponse.setLastPage(page.isLast());

		return productResponse;
	}

	@Override
	public ProductResponse getProductByKeywordService(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
		Sort sortAndOrder = sortOrder.equalsIgnoreCase("asc") 
				? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();
		Pageable pageable = PageRequest.of(pageNumber, pageSize, sortAndOrder);
		Page<Product> page = productRepository.findByProductNameIgnoreCaseContaining(keyword, pageable);
		
		List<Product> products = page.getContent();
		
		
		// Here, we need to check whether there is any product in database or not
		if(products.isEmpty())
			throw new APIException("No product with specified keyword exists!!");
		
		List<ProductDTO> productDTOs = products.stream().map(product -> modelMapper.map(product, ProductDTO.class))
				.toList();

		ProductResponse productResponse = new ProductResponse();
		productResponse.setContent(productDTOs);
		productResponse.setPageNumber(pageNumber);
		productResponse.setPageSize(pageSize);
		productResponse.setTotalElements(page.getTotalElements());
		productResponse.setTotalPages(page.getTotalPages());
		productResponse.setLastPage(page.isLast());
		
		return productResponse;
	}

	@Override
	public ProductDTO updateProductService(Long productId, Product product) {
		Product oldProduct = productRepository.findById(productId)
								.orElseThrow(() -> new ResourceNotFoundException("Product", productId, "productId"));
	
		oldProduct.setProductName(product.getProductName());
		oldProduct.setDescription(product.getDescription());
		oldProduct.setQuantity(product.getQuantity());
		oldProduct.setPrice(product.getPrice());
		oldProduct.setDiscount(product.getDiscount());
		oldProduct.setSpecialPrice(product.getPrice() - (product.getPrice() * product.getDiscount() * 0.01));
		
		Product savedProduct = productRepository.save(oldProduct);
		
		return modelMapper.map(savedProduct, ProductDTO.class);
	}

	@Override
	public ProductDTO deleteProductService(Long productId) {
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("Product", productId, "productId"));
		
		productRepository.deleteById(productId);
		
		return modelMapper.map(product, ProductDTO.class);
	}

	@Override
	public APIResponse getProductsCountService() {
		return new APIResponse("Total products count: "+productRepository.count(), true);
	}

	@Override
	public ProductDTO updateProductImageService(Long productId, MultipartFile productImage) {
		// Get the product from DB
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("Product", productId, "productId"));
		
		/* Upload image to server (for now we will be uploading to server by creating a folder but 
		 * later on we can upload to some media server also say like S3 bucket). Thereafter get
		 *  the file name of uploaded image
		 */
		// String path = "images/";   -> Getting from application.properties file
		String filename = fileService.uploadImage(path, productImage);		
		
		
		// Updating the new file name to the product and save the product
		product.setImage(filename);
		Product savedProduct = productRepository.save(product);
		
		
		// return DTO after mapping product to DTO
		return modelMapper.map(savedProduct, ProductDTO.class);
	}
}
