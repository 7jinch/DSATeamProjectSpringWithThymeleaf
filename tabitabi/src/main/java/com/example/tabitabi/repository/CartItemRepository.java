package com.example.tabitabi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.tabitabi.model.cart.Cart;
import com.example.tabitabi.model.cart.CartItem;
import com.example.tabitabi.model.member.Member;

public interface CartItemRepository extends JpaRepository<CartItem, Long>{

	List<CartItem> findByCart(Cart findCart);

	@Modifying
	@Query("DELETE FROM CartItem ci WHERE ci.cart.id = :cardId AND ci.product.productId IN :productIdList")
	void deleteByCartIdAndProductIds(@Param("cardId") Long cardId, @Param("productIdList") List<Long> productIdList);

	void deleteByCartId(Long id);
}
