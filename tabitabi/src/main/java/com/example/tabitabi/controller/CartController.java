package com.example.tabitabi.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.example.tabitabi.DTO.CartItemAndImageDTO;
import com.example.tabitabi.model.Product.Product;
import com.example.tabitabi.model.Product.ProductImage;
import com.example.tabitabi.model.cart.Cart;
import com.example.tabitabi.model.cart.CartItem;
import com.example.tabitabi.model.member.LoginForm;
import com.example.tabitabi.model.member.Member;
import com.example.tabitabi.service.CartService;
import com.example.tabitabi.service.MemberService;
import com.example.tabitabi.service.ProductService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("cart")
@RequiredArgsConstructor
public class CartController {
	private final MemberService memberService; // 회원 서비스 의존성 주입
	private final ProductService productService; // 상품 서비스 의존성 주입
	private final CartService cartService; // 장바구니 서비스 의존성 주입
	
	// 장바구니 페이지 열기
	@GetMapping("{member_id}")
	public String showCart(@PathVariable(name="member_id") Long member_id, // 경로변수 받아오기,
			HttpServletRequest req, HttpSession session,
			@SessionAttribute(name = "loginMember", required = false) Member loginMember,
			Model model) {
		log.info("showCart 실행");
		log.info("장바구니 페이지 열기");
		log.info("받은 회원 id: {}", member_id);
		session = req.getSession(); // 세션 받아오기
		
		Member member = (Member)session.getAttribute("loginMember"); // member 객체 받아오기
//		if(!member.getId().equals(member_id)) {
//	        model.addAttribute("loginForm", new LoginForm());
//			return "product/product_list"; // 로그인한 회원과 조회하려는 회원이 다르면 리스트 페이지로
//		}
		
		List<CartItem> cartItems = cartService.findByMember(member);
		
		List<CartItemAndImageDTO> ciai = new ArrayList<>();
		for (CartItem item : cartItems) {
			Long id = item.getProduct().getProductId();
			Product product = productService.findProduct(id); // 제품 ID로 제품 조회
			ProductImage productImage = productService.findFileByProductId(product); // 제품 ID로 파일 조회
			
			CartItemAndImageDTO cai = new CartItemAndImageDTO();
			cai.setCartItem(item);
			cai.setProductImage(productImage);
			ciai.add(cai);
		}
		
//		for(CartItemAndImageDTO c : ciai) {
//			log.info("이미지 데이터: {}", c.getProductImage());
//			log.info("상품 데이터: {}", c.getCartItem());
//			log.info("\n");
//		}
		model.addAttribute("cartList", ciai);
		model.addAttribute("memberId", member.getId());
		log.info("장바구니 페이지 열기");
		return "cart/cart";
	}
	
	// 장바구니에 추가
	@PostMapping()
	public ResponseEntity<?> addCart(
			@Validated @RequestBody Map<String, Long> requestBody, BindingResult result,
			@SessionAttribute(name = "loginMember", required = false) Member loginMember
			){
		log.info("addCart 실행");
		if (result.hasErrors()) return ResponseEntity.badRequest().body(Map.of("message", "장바구니 추가 중 에러가 발생했습니다."));
		if(loginMember == null) return ResponseEntity.badRequest().body(Map.of("message", "로그인이 필요합니다."));
		Long productId = requestBody.get("productId"); // productId 가져오기
		log.info("받은 회원 id 데이터: {}", loginMember.getId());
		log.info("받은 상품 id 데이터: {}", productId);
		
		Member findMember = memberService.findMemberById(loginMember.getId()); // 현재 세션에서 member 받아오기
		Product findProduct = productService.findProductById(productId); // product 받아오기
		Cart findCart = cartService.findCartByMemberId(findMember.getId()); // 로그인한 회원의 cart 받아오기

		Boolean check = cartService.checkDuplicate(findCart, findProduct); // 이미 담겨져 있는지 검사: 장바구니가 비어있으면 true, 장바구니에 같은 상품이 없어도 true
		if(check) {
			cartService.createCartItemByCart(findCart, findProduct); // 장바구니에 추가
			log.info("장바구니에 추가");
			return ResponseEntity.ok(Map.of("message", "장바구니에 추가했습니다."));
		}
		else return ResponseEntity.badRequest().body(Map.of("message", "이미 장바구니에 추가된 상품입니다."));
	}
	
	// 선택한 상품을 장바구니에서 삭제하기
	@DeleteMapping("{member_id}")
	public ResponseEntity<?> deleteCartProducts(@Validated @PathVariable(name="member_id") Long member_id, // 경로변수 받아오기,
												@RequestBody List<String> checkedProducts, // 삭제할 상품 리스트 가져오기
												@SessionAttribute(name = "loginMember", required = false) Member loginMember, // 세션 가져오기
												BindingResult result
			) {
		log.info("deleteCartProducts 실행");
		if (result.hasErrors()) return ResponseEntity.badRequest().body(Map.of("message", "장바구니 삭제 중 에러가 발생했습니다."));
		if(loginMember == null) return ResponseEntity.badRequest().body(Map.of("message", "로그인이 필요합니다."));
		
		log.info("받은 회원 id: {}", member_id);
		
		List<Long> productIdList = new ArrayList<>();
		for(String id: checkedProducts) productIdList.add(Long.parseLong(id));
		log.info("받은 상품 id: {}", productIdList);
		
		Member findMember = memberService.findMemberById(loginMember.getId()); // 현재 세션에서 member 받아오기
		if(!findMember.getId().equals(member_id)) return ResponseEntity.badRequest().body(Map.of("message", "로그인이 필요합니다."));
		Cart cart = cartService.findCartByMemberId(findMember.getId()); // 로그인한 회원의 cart 가져오기
		
		//
		cartService.deleteCartItemByCart(cart, productIdList);
		
		log.info("장바구니에서 삭제했습니다.");
		return ResponseEntity.ok(Map.of("message", "장바구니에서 삭제했습니다."));
	}
	
	// 장바구니 비우기
	@DeleteMapping("{member_id}/all")
	public ResponseEntity<?> deleteAllCartProducts(@PathVariable(name="member_id") Long member_id, // 경로변수 받아오기,
											       @SessionAttribute(name = "loginMember", required = false) Member loginMember // 세션 가져오기
	) {
		log.info("deleteAllCartProducts 실행");
		if(loginMember == null) return ResponseEntity.badRequest().body(Map.of("message", "로그인이 필요합니다."));
	
		log.info("받은 회원 id: {}", member_id);
		
		Member findMember = memberService.findMemberById(loginMember.getId()); // 현재 세션에서 member 받아오기
		if(!findMember.getId().equals(member_id)) return ResponseEntity.badRequest().body(Map.of("message", "로그인이 필요합니다."));
		Cart cart = cartService.findCartByMemberId(findMember.getId()); // 로그인한 회원의 cart 가져오기
		
		cartService.deleteAllCartProducts(cart);
		
		log.info("장바구니를 비웠습니다.");
		return ResponseEntity.ok(Map.of("message", "장바구니를 비웠습니다."));
	}
}
