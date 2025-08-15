package com.cs.ecommerce.orderservice.repository;

import com.cs.ecommerce.orderservice.entities.CartItem;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByIdAndCartUserId(Long itemId, Long userId);

    @EntityGraph(attributePaths = "cart")
    List<CartItem> findByCartUserId(Long userId);

    Optional<CartItem> findByCartUserIdAndProductId(Long userId, Long productId);

    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cart.userId = :userId")
    void deleteAllByUserId(@Param("userId")Long userId);

    @Query("SELECT SUM(ci.quantity) FROM CartItem ci WHERE ci.cart.userId = :userId")
    Integer countItemsByUserId(@Param("userId") Long userId);

    boolean existsByIdAndCartUserId(Long itemId, Long userId);
}
