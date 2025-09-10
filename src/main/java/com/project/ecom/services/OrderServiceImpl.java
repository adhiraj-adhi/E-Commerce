package com.project.ecom.services;

import com.project.ecom.dao.*;
import com.project.ecom.exceptions.ResourceNotFoundException;
import com.project.ecom.model.*;
import com.project.ecom.payload.OrderDTO;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ModelMapper modelmapper;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CartService cartService;


    @Override
    @Transactional
    /*
    * If we are doing multiple operations in a single method and these operations are important from
    * the database consistency standpoint. We would want all the operations to succeed at once.
    * If anything fails, the entire like all the operations should roll back.
    * So for that purpose we're going to add transactional annotation over here.
    */
    public OrderDTO placeOrder(String username, Long addressId, String paymentMethod, String paymentGateway, String transactionId, String status, String responseMessage) {
        /*
        * (Q) Where is the product related information?
        * We have cart in our application that user has created, wherein he might add some products and
        * he'll go to the checkout page as the next step.
        * Checkout is the next step generally in all the e-commerce websites.
        * What we will be doing is we'll be converting the cart into order,
        * and the product related information will come in from the user's cart.
        * So cart basically will be moved to the order. That's how it's going to work in our system.
        * And if the user does not want any product that is added in the cart to be ordered,
        * he can remove the products from the cart and then he can proceed further for the ordering.
        * So that is how it's going to work at our end.
        * */

        /*
        * STEPS WE WILL BE FOLLOWING:
        * 1. Getting user cart
        * 2. Get item from cart into the order items
        * 3. Update product stock
        * 4. Clear the cart and update the total
        * 5. Send back the order details.
        * */

        // Getting user cart:
        Cart userCart = cartRepository.findCartByUsername(username);
        if (userCart == null) {
            throw new ResourceNotFoundException("Cart", username, "username");
        }

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", addressId, "addressId"));

        Order order = new Order();
        order.setUsername(username);
        order.setAddress(address);
        order.setOrderDate(LocalDate.now());
        order.setTotalAmount(userCart.getTotalPrice());
        order.setOrderStatus("Order Accepted !");
        /*
        * The status is important because tomorrow if we make an admin panel, we can have a functionality
        * wherein we can allow the administrators to update the order status or order status gets updated on
        * a certain events. For example, we have an admin panel and the admin is processing the order.
        * So he receives the order. Order is accepted by the system. Then we will start dispatching the order.
        * So the admin will receive the order. And he'll change the status to order processing. And he'll start packing
        * the order. Once the order is packed, it is handed over to the shipment service or the courier and the admin
        * can update the status in the admin panel to that of shipped. Similarly, he can update it to delivered once the
        * order is delivered.
        * */

        Payment payment = new Payment();
        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentStatus(status);
        payment.setTransactionId(transactionId);
        payment.setResponseMessage(responseMessage);
        payment.setPaymentGateway(paymentGateway);
        payment.setOrder(order); // Set the bidirectional relationship
        Payment savedPayment = paymentRepository.save(payment); // Save payment first to generate paymentId

        order.setPayment(savedPayment); // We will set saved payment
        Order savedOrder = orderRepository.save(order); // Save the order to generate orderId
        Order savedEffectivelyFinalOrder = savedOrder; // Make savedOrder effectively final for use in lambda

        List<OrderItem> orderItemList = userCart.getCartItems().stream().map(cartItem -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setOrderedProductPrice(cartItem.getSpecialPrice());
            /* orderItem.setOrder(savedOrder); // We can not do this to set bidirectional relationship as savedOrder
               is not final or effectively final (because we are modifying it). So, we will use savedEffectivelyFinalOrder.
             */
            orderItem.setOrder(savedEffectivelyFinalOrder); // Set the bidirectional relationship
            Product product = cartItem.getProduct();
            product.setQuantity(product.getQuantity() - cartItem.getQuantity());
            productRepository.save(product); // Updating the product stock

            return orderItem;
        }).collect(Collectors.toList());

        List<OrderItem> savedOrderItemList = orderItemRepository.saveAll(orderItemList); // Save all order items [But, do we need this line?]

        savedOrder.setOrderItems(savedOrderItemList); // Set the saved order items to the order
        savedOrder = orderRepository.save(savedOrder); // Save the order again with order items

        // Way 1: Didn't work
//        userCart.getCartItems().forEach(cartItem -> {
//            userCart.getCartItems().remove(cartItem);  // it will throw error as we are modifying the collection while iterating it.
//        });

        // Way 2: Didn't work
//        userCart.getCartItems().removeAll(userCart.getCartItems());

        // Way 3: Didn't work
//        Iterator<CartItem> iterator = userCart.getCartItems().iterator();
//        while (iterator.hasNext()) {
//            CartItem cartItem = iterator.next();
//            iterator.remove();   // removes from collection safely
//            cartItem.setCart(null); // break relationship
//        }

        // Way 4: Didn't work
        Iterator<CartItem> iterator = userCart.getCartItems().iterator();
        while (iterator.hasNext()) {
            CartItem cartItem = iterator.next();
            cartItemRepository.deleteByCartItemIdDirectly(cartItem.getCartItemId());
            // Deleting the cart item from the repository
            iterator.remove();
        }

        // Way 5: Didn't work
        // Create a new list to avoid ConcurrentModificationException
//        List<CartItem> itemsToRemove = new ArrayList<>(userCart.getCartItems());
//
//        for (CartItem cartItem : itemsToRemove) {
//            userCart.getCartItems().remove(cartItem);   // Remove from parent collection
//            cartItem.setCart(null);                 // Break child-to-parent link
//        }

        // Way 6: Doesn't Work - ConcurrentModificationException
//        userCart.getCartItems().forEach(cartItem -> {
//            cartService.deleteFromCart(cartItem.getProduct().getProductId());
//        });


        userCart.setTotalPrice(0.0); // Reset total price
        cartRepository.save(userCart); // Save the updated cart

        OrderDTO orderDTO = modelmapper.map(savedOrder, OrderDTO.class);
//        orderItemList.forEach(orderItem -> {
//            orderDTO.getOrderItems().add(modelmapper.map(orderItem, OrderItemDTO.class));
//        });
        orderDTO.setAddressId(addressId);

        return orderDTO;
    }
}
