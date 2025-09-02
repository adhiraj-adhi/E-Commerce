package com.project.ecom.services;

import com.project.ecom.dao.CartItemRepository;
import com.project.ecom.dao.CartRepository;
import com.project.ecom.dao.ProductRepository;
import com.project.ecom.exceptions.APIException;
import com.project.ecom.exceptions.ResourceNotFoundException;
import com.project.ecom.model.Cart;
import com.project.ecom.model.CartItem;
import com.project.ecom.model.Product;
import com.project.ecom.model.User;
import com.project.ecom.payload.CartDTO;
import com.project.ecom.payload.CartItemDTO;
import com.project.ecom.payload.ProductDTO;
import com.project.ecom.util.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private AuthUtil authUtil;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {
        // First we need to check if user has cart created, if not we need to create one
        // We need to get the Product details
        // Perform validations as in like quantity<=stock
        // Create Cart Item
        // Save Cart Item
        // Return updated cart info
        // Let us start:

        // Finding existing cart or creating one:
        Cart userCart = getOrCreateCart();

        // Retrieving product details:
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", productId, "productId"));

        // Perform Validations:
        // Validation 1: If stock is available:
        if (product.getQuantity()==0) {
            throw new APIException("Out of Stock");
        }
        if (product.getQuantity()<quantity) {
            throw new APIException("Requested quantity is not available in stock. Please make order less than equal to quantity:"+product.getQuantity());
        }
        // Validation 2: If product is already in cart:
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(productId, userCart.getCartId());  // checking corresponding to user cart only
        if (cartItem!=null) {
            // we will just increase the count since product is already in cart
            if (product.getQuantity()-cartItem.getQuantity()==0) {
                throw new APIException("Please proceed with cart orders as stock limit is reached");
            }
            if (cartItem.getQuantity()+quantity>product.getQuantity()) {
                throw new APIException("Requested quantity is not available in stock. Please make order less than equal to quantity:"+(product.getQuantity()-cartItem.getQuantity()));
            }
            cartItem.setQuantity(cartItem.getQuantity()+quantity);
            cartItemRepository.save(cartItem);

            userCart.setTotalPrice(userCart.getTotalPrice()+(quantity*product.getSpecialPrice()));
            userCart.getCartItems().add(cartItem);
            cartRepository.save(userCart);
            return modelMapper.map(userCart, CartDTO.class);
        }


        // Create Cart Item:
        CartItem newCartItem = new CartItem();
        newCartItem.setProduct(product);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setQuantity(quantity);
        newCartItem.setSpecialPrice(product.getSpecialPrice());
        newCartItem.setCart(userCart);
        cartItemRepository.save(newCartItem);

        /*
        * NOTE: As of now user has not placed the order so we will
        * not decreasing the quantity of product in the database.
        * */

        // Updating the user cart:
        userCart.setTotalPrice(userCart.getTotalPrice()+(quantity*product.getSpecialPrice()));
        userCart.getCartItems().add(newCartItem);
        cartRepository.save(userCart);

        // Returning CartDTO
        CartDTO cartDTO = modelMapper.map(userCart, CartDTO.class);
        // Manually mapping cart items:
        List<CartItemDTO> cartItemDTOS = userCart.getCartItems().stream()
                .map(cartItm -> {
                    CartItemDTO cartItemDTO = modelMapper.map(cartItm, CartItemDTO.class);
                    cartItemDTO.setSpecialPrice(product.getSpecialPrice());
                    cartItemDTO.setProductDTO(modelMapper.map(cartItm.getProduct(), ProductDTO.class));
                    cartItemDTO.getProductDTO().setQuantity(cartItemDTO.getQuantity()); // here quantity of product is for the quantity added to cart
                    return cartItemDTO;
                })
                .collect(Collectors.toList());
        cartDTO.setCartItemDTOs(cartItemDTOS);
        return cartDTO;
    }

    @Override
    public List<CartDTO> getAllCartItems() {
        List<Cart> carts = cartRepository.findAll();

        if (carts.isEmpty()) throw new APIException("Cart does not exist");

        List<CartDTO> cartDTOS = carts.stream()
                .map(cart -> {
                    CartDTO cartDTO = new CartDTO();
                    cartDTO.setCartId(cart.getCartId());
                    cartDTO.setTotalPrice(cart.getTotalPrice());

                    // map cart items manually
                    List<CartItemDTO> cartItemDTOS = cart.getCartItems().stream()
                            .map(cartItem -> {
                                CartItemDTO cartItemDTO = new CartItemDTO();
                                cartItemDTO.setCartItemId(cartItem.getCartItemId());
                                cartItemDTO.setQuantity(cartItem.getQuantity());
                                cartItemDTO.setSpecialPrice(cartItem.getProduct().getPrice());
                                cartItemDTO.setProductDTO(
                                        new ProductDTO(cartItem.getProduct().getProductId(), cartItem.getProduct().getProductName(), cartItem.getProduct().getImage(), cartItem.getProduct().getQuantity(), cartItem.getProduct().getPrice(), cartItem.getProduct().getDiscount(), cartItem.getProduct().getSpecialPrice()));
                                return cartItemDTO;
                            })
                            .collect(Collectors.toList());

                    cartDTO.setCartItemDTOs(cartItemDTOS);
                    return cartDTO;
                })
                .collect(Collectors.toList());

        return cartDTOS;
    }

    @Override
    public CartDTO getUsersCartItems() {
        Cart cart = cartRepository.findCartByUsername(authUtil.loggedInUser().getUsername());

        if (cart==null) throw new APIException("Cart does not exist");

        CartDTO cartDTO = new CartDTO();
        cartDTO.setCartId(cart.getCartId());
        cartDTO.setTotalPrice(cart.getTotalPrice());

        // map cart items manually
        List<CartItemDTO> cartItemDTOS = cart.getCartItems().stream()
                .map(cartItem -> {
                    CartItemDTO cartItemDTO = new CartItemDTO();
                    cartItemDTO.setCartItemId(cartItem.getCartItemId());
                    cartItemDTO.setSpecialPrice(cartItem.getProduct().getSpecialPrice());
                    cartItemDTO.setDiscount(cartItem.getProduct().getDiscount());
                    cartItemDTO.setQuantity(cartItem.getQuantity());
                    cartItemDTO.setProductDTO(
                            new ProductDTO(
                                    cartItem.getProduct().getProductId(),
                                    cartItem.getProduct().getProductName(),
                                    cartItem.getProduct().getImage(),
                                    cartItem.getQuantity(),  // setting the quantity to that in cartItem
                                    cartItem.getProduct().getPrice(),
                                    cartItem.getProduct().getDiscount(),
                                    cartItem.getProduct().getSpecialPrice()
                            )
                    );
                    return cartItemDTO;
                })
                .collect(Collectors.toList());

        cartDTO.setCartItemDTOs(cartItemDTOS);

        return cartDTO;
    }

    @Override
    public CartDTO updateProductInCart(Long productId, String operation) {
        Cart cart = authUtil.loggedInUser().getCart();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId, "product"));
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(productId, cart.getCartId());

        if (operation.equals("increase")) {
            // Validation:
            if (cartItem.getQuantity()+1>product.getQuantity()) {
                throw new APIException("Please proceed with cart orders as stock limit is reached");
            }
            cartItem.setQuantity(cartItem.getQuantity()+1);
            cartItemRepository.save(cartItem);
            cart.setTotalPrice(cart.getTotalPrice()+product.getSpecialPrice());
            cartRepository.save(cart);
        } else {
            // Validation
            if (cartItem.getQuantity()-1==0) {
                cart.setTotalPrice(cart.getTotalPrice() - cartItem.getSpecialPrice() * cartItem.getQuantity());

                // Delete from DB first
                cartItemRepository.deleteById(cartItem.getCartItemId());

                // Then update in-memory list
                cart.getCartItems().remove(cartItem);
            } else {
                cartItem.setQuantity(cartItem.getQuantity()-1);
                cartItemRepository.save(cartItem);
                cart.setTotalPrice(cart.getTotalPrice()-product.getSpecialPrice());
            }
            cartRepository.save(cart);
        }

        cart.setCartItems(cart.getCartItems());
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        List<CartItemDTO> cartItemDTOS = cart.getCartItems().stream()
                .map(cartItm -> {
                    CartItemDTO cartItemDTO = modelMapper.map(cartItm, CartItemDTO.class);
                    cartItemDTO.setSpecialPrice(product.getSpecialPrice());
                    cartItemDTO.setProductDTO(modelMapper.map(cartItm.getProduct(), ProductDTO.class));
                    cartItemDTO.getProductDTO().setQuantity(cartItem.getQuantity()); // here quantity of product is for the quantity added to cart
                    return cartItemDTO;
                })
                .collect(Collectors.toList());
        cartDTO.setCartItemDTOs(cartItemDTOS);
        return cartDTO;
    }

    @Override
    public CartItemDTO deleteFromCart(Long productId) {
        Cart cart = authUtil.loggedInUser().getCart();

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(productId, cart.getCartId());
        if (cartItem == null) {
            throw new APIException("Cart item not found");
        }

        cart.getCartItems().remove(cartItem);
        double newTotal = cart.getTotalPrice() - (cartItem.getQuantity() * cartItem.getSpecialPrice());
        cart.setTotalPrice(Math.max(newTotal, 0));
        cartItemRepository.deleteById(cartItem.getCartItemId());
        cartRepository.save(cart);

        CartItemDTO cartItemDTO = modelMapper.map(cartItem, CartItemDTO.class);
        cartItemDTO.setProductDTO(modelMapper.map(cartItem.getProduct(), ProductDTO.class));
        return cartItemDTO;
    }


    // Getting All Card Items:

    private Cart getOrCreateCart() {
        User user = authUtil.loggedInUser();
        Cart userCart = cartRepository.findCartByUsername(user.getUsername());
        if (userCart!=null) {
            return userCart;
        }
        Cart cart = new Cart();
        cart.setTotalPrice(0.0);
        cart.setUser(user);
        return cartRepository.save(cart);
    }
}
