package com.easybuy.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;

import com.easybuy.entity.Cart;
import com.easybuy.dto.CartDTO;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    // ✅ DELETE
    @Modifying
    @Transactional
    @Query("DELETE FROM Cart c WHERE c.userId = :userId AND c.productId = :productId")
    void deleteByUserIdAndProductId(@Param("userId") String userId,
                                   @Param("productId") Long productId);

    // ✅ GET CART WITH PRODUCT (FIXED)
    @Query(value =
            "SELECT " +
            "c.product_id as productId, " +
            "p.name as name, " +
            "p.price as price, " +
            "p.image as image, " +
            "p.brand as brand, " +
            "p.category as category, " +
            "p.color as color, " +
            "p.description as description, " +
            "c.quantity as quantity, " +
            "'Guest' as userName " +
            "FROM cart c " +
            "JOIN product p ON c.product_id = p.id " +
            "WHERE c.user_id = :userId",
            nativeQuery = true)
    List<CartDTO> getCartWithProduct(@Param("userId") String userId);

    // ✅ FIND
    List<Cart> findByUserId(String userId);

    // ✅ DUPLICATE CHECK
    Cart findByUserIdAndProductId(String userId, Long productId);
}