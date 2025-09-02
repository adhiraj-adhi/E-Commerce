package com.project.ecom.services;

import com.project.ecom.dao.CartItemRepository;
import com.project.ecom.dao.CartRepository;
import com.project.ecom.dao.CategoryRepository;
import com.project.ecom.dao.ProductRepository;
import com.project.ecom.exceptions.APIException;
import com.project.ecom.exceptions.ResourceNotFoundException;
import com.project.ecom.model.*;
import com.project.ecom.payload.APIResponse;
import com.project.ecom.payload.ProductDTO;
import com.project.ecom.payload.ProductResponse;
import com.project.ecom.util.AuthUtil;
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
import java.util.stream.Collectors;

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

	@Autowired
	private AuthUtil authUtil;

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private CartItemRepository cartItemRepository;
	
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
		
		if(isProductPresent) throw new APIException("Product already exists!!");

		// Getting seller information:
		User seller = authUtil.loggedInUser();

		if (seller==null) throw new APIException("Unable to load seller details");
		product.setCategory(category);
		product.setImage("default.png");
		double specialPrice = product.getPrice() - (product.getPrice() * product.getDiscount() * 0.01);
		product.setSpecialPrice(specialPrice);
		product.setSeller(seller);

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
		// A seller can update only his product
		User seller = authUtil.loggedInUser();
//		Product oldProduct = seller.getProducts().stream()
//				.filter(product1 -> product1.getProductId().equals(productId))
//				.findFirst()
//				.orElseThrow(() -> new ResourceNotFoundException("Product", productId, "productId"));
		/*
		* The above approach may not work if user is fetched but product is being fetched
		* lazily. We can even not do fetch type as eager because if there are many products
		* associated with seller than while loading seller all the products will also
		* get loaded (although this may be not required)
		*  */

//		Product oldProduct = productRepository.findById(productId)
//								.orElseThrow(() -> new ResourceNotFoundException("Product", productId, "productId"));

		Product oldProduct = productRepository.findBySellerIdAndProductId(seller.getUserId(), productId);
		if (oldProduct==null) throw new ResourceNotFoundException("Product", productId, "productId");

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
		// If user is admin than directly delete the product
		// Else check if product is associated with logged in user
		User loggedInUser = authUtil.loggedInUser();

		Product product;

		if (loggedInUser.getRoles().stream()
				.anyMatch(role -> role.getRole().equals(AppRole.ROLE_ADMIN))) {
			product = productRepository.findById(productId)
					.orElseThrow(() -> new ResourceNotFoundException("Product", productId, "productId"));
		} else {
			product = productRepository.findBySellerIdAndProductId(loggedInUser.getUserId(), productId);
			if (product == null) {
				throw new ResourceNotFoundException("Product", productId, "productId");
			}
		}

		// Since we are deleting the product, we need to delete the product from cartItem and cart also:
		// Getting CartItems associated with this product:
		List<CartItem> cartItems = cartItemRepository.findCartItemByProductId(product.getProductId());

		// Getting Carts associated with these cartItems:
		List<Cart> carts = cartItems.stream().map(cartItem -> cartItem.getCart()).collect(Collectors.toList());
		// Getting Carts associated with these cartItems:
		for (Cart cart : carts) {
			// Calculate the total price adjustment before removing the CartItem
			cart.setTotalPrice(cart.getTotalPrice() - (product.getSpecialPrice() * cart.getCartItems().stream()
					.filter(cartItem -> cartItem.getProduct().getProductId().equals(product.getProductId()))
					.findFirst()
					.map(CartItem::getQuantity)
					.orElse(0)));

			// Remove the CartItems associated with the product
			cart.getCartItems().removeIf(cartItem -> cartItem.getProduct().getProductId().equals(product.getProductId()));

			// Save the updated cart
			cartRepository.save(cart);
		}

		// Now delete all the cartItems associated with this product
		cartItemRepository.deleteByProduct(product);

		// Now delete the product
		productRepository.delete(product);

		return modelMapper.map(product, ProductDTO.class);
	}

	@Override
	public APIResponse getProductsCountService() {
		return new APIResponse("Total products count: "+productRepository.count(), true);
	}

	@Override
	public ProductDTO updateProductImageService(Long productId, MultipartFile productImage) {
		// A seller can update only his product
		User seller = authUtil.loggedInUser();

		// Get the product corresponding to seller from DB
		Product product = productRepository.findBySellerIdAndProductId(seller.getUserId(), productId);
		if (product==null) throw new ResourceNotFoundException("Product", productId, "productId");

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
