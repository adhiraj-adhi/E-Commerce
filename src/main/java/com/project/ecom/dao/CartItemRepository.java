package com.project.ecom.dao;

import com.project.ecom.model.CartItem;
import com.project.ecom.model.Product;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
//    @Query("SELECT ci FROM CartItem ci WHERE ci.product.productId=?1 AND ci.cart.cartId=?2")
    @Query("SELECT ci FROM CartItem ci WHERE ci.product.productId=?1 AND ci.cart.cartId=?2")
    CartItem findCartItemByProductIdAndCartId(Long productId, Long cartId);

    @Transactional
    void deleteByProduct(Product product);

    @Query("SELECT ci FROM CartItem ci WHERE ci.product.productId=?1")
    List<CartItem> findCartItemByProductId(Long productId);

    @Transactional
    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cartItemId = :cartItemId")
    void deleteByCartItemIdDirectly(@Param("cartItemId") Long cartItemId);
}