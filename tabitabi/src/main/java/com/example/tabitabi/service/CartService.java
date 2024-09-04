package com.example.tabitabi.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.tabitabi.model.Product.Product;
import com.example.tabitabi.model.cart.Cart;
import com.example.tabitabi.model.cart.CartItem;
import com.example.tabitabi.model.member.Member;
import com.example.tabitabi.repository.CartItemRepository;
import com.example.tabitabi.repository.CartRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {
	private final CartRepository cartRepository;
	private final CartItemRepository cartItemRepository;
	
	// 장바구니 생성
	@Transactional
	public void createCart(Member member) {
		Cart cart = new Cart();
		cart.setMember(member);
		cartRepository.save(cart);
	}

	// 장바구니 찾기
	public Cart findCartByMemberId(Long member_id) {
		Cart cart = cartRepository.findByMemberId(member_id);
		return cart;
	}

	// 장바구니에 아이템 추가
	@Transactional
	public void createCartItemByCart(Cart cart, Product product) {
		CartItem cartItem = new CartItem();
		cartItem.setCart(cart);
		cartItem.setProduct(product);
		cartItemRepository.save(cartItem);
	}

	// 장바구니 중복 아이템 여부 체크
	public Boolean checkDuplicate(Cart findCart, // 로그인한 회원의 장바구니
								  Product findProduct // 장바구니에 추가할 상품
			) {
		List<CartItem> cartItems = (List<CartItem>) cartItemRepository.findByCart(findCart);
		log.info("찾은 카트 아이템: {}", cartItems);
		if(cartItems == null || cartItems.size() == 0) return true; // 장바구니가 비어있으면 바로 추가
		for(CartItem ci : cartItems) {
			if(ci.getProduct().getProductId().equals(findProduct.getProductId())) return false; // 장바구니에 같은 상품이 있으면 취소
		}
		return true;
	}

	// 장바구니 상품 조회
	public List<CartItem> findByMember(Member member) {
		Cart cart = cartRepository.findByMemberId(member.getId());
		List<CartItem> CartItems = cartItemRepository.findByCart(cart);
		return CartItems;
	}

	// 장바구니 상품 삭제
	@Transactional
	public Boolean deleteCartItemByCart(Cart cart, List<Long> productIdList) {
		cartItemRepository.deleteByCartIdAndProductIds(cart.getId(), productIdList);
		return true;
	}

	// 장바구니 비우기
	@Transactional
	public void deleteAllCartProducts(Cart cart) {
		cartItemRepository.deleteByCartId(cart.getId());
	}
}
