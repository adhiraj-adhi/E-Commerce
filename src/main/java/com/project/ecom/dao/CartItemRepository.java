package com.project.ecom.dao;

import com.project.ecom.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
//    @Query("SELECT ci FROM CartItem ci WHERE ci.product.productId=?1 AND ci.cart.cartId=?2")
    @Query("SELECT ci FROM CartItem ci WHERE ci.product.productId=?1 AND ci.cart.cartId=?2")
    CartItem findCartItemByProductIdAndCartId(Long productId, Long cartId);
}
