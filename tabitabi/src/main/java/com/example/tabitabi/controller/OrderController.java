package com.example.tabitabi.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.example.tabitabi.DTO.OrderAndOrderItemsDTO;
import com.example.tabitabi.DTO.OrderListDTO;
import com.example.tabitabi.DTO.ProductAndImageDTO;
import com.example.tabitabi.model.Product.ProductImage;
import com.example.tabitabi.model.cart.CartItem;
import com.example.tabitabi.model.member.Member;
import com.example.tabitabi.model.order.OrderItems;
import com.example.tabitabi.model.order.OrderTable;
import com.example.tabitabi.model.orderAddress.OrderAddress;
import com.example.tabitabi.model.seller.LoginForm;
import com.example.tabitabi.service.CartService;
import com.example.tabitabi.service.MemberService;
import com.example.tabitabi.service.OrderAddressService;
import com.example.tabitabi.service.OrderTableService;
import com.example.tabitabi.service.ProductService;

import jakarta.validation.Valid;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("order")
@RequiredArgsConstructor
public class OrderController {
	private final MemberService memberService;
	private final OrderTableService orderTableService;
	private final OrderAddressService addressService;
	private final ProductService productService;
	private final CartService cartService;

	// 주문서 작성 페이지 조회
	@GetMapping("{orderId}")
	public String orderFormPage(@PathVariable(name = "orderId") Long orderId, Model model,
			@SessionAttribute(name = "loginMember", required = false) Member loginMember) {
		log.info("주문서 작성 페이지 열기");
		if(loginMember == null) {
			model.addAttribute("loginForm", new LoginForm());
			return "member/loginForm"; // 로그인하지 않은 상태라면 로그아웃
		}
		
		List<OrderTable> otlist = orderTableService.findByMember(loginMember);
		for(OrderTable ot: otlist) {
			if(ot.getOrder_status().equals("주문서 작성 중")) {
				if(ot.getId().equals(orderId)) continue;
				else return "redirect:/order/" + ot.getId();
			}
		}
		
		Member findMember = memberService.findMemberById(loginMember.getId());
		OrderTable findOrder = orderTableService.findById(orderId);
		model.addAttribute("member", findMember); // 회원 정보 담기

		List<OrderAddress> addressList = addressService.findbyMember(findMember);
		model.addAttribute("addressList", addressList); // 배송지 리스트 담기

		List<OrderItems> orderItemList = orderTableService.findOrderItemsByOrder(findOrder);
		List<ProductAndImageDTO> productAndImageList = new ArrayList<>(); // 빈 리스트 생성
		
		for(OrderItems ot : orderItemList) {
			ProductAndImageDTO productAndImage = new ProductAndImageDTO();
			ProductImage productImage = productService.findFileByProductId(ot.getProduct()); // 상품 이미지 객체 가져오기
			
			productAndImage.setProductImage(productImage); // 상품 이미지
			productAndImage.setProduct(ot.getProduct()); // 상품
			productAndImage.setQuantity(ot.getQuantity()); // 주문 수량
			
			productAndImageList.add(productAndImage); // 리스트에 값 추가
		}
		
		model.addAttribute("orderItemList", productAndImageList); // 주문할 상품 리스트 담기
		
		OrderTable orderTable = orderTableService.findById(orderId);
		model.addAttribute("order", orderTable); // 주문 담기
		
//		for(ProductAndImageDTO ot : productAndImageList) {
//			log.info("주문할 상품: {}", ot.getProduct());
//			log.info("주문할 상품 이미지: {}", ot.getProductImage());
//			log.info("주문할 상품 수량: {}", ot.getQuantity());
//		}
		return "order/order";
	}

	// 주문서 생성
	@PostMapping("")
	public ResponseEntity<?> orderCreate(@Valid @RequestBody OrderListDTO orderListDTO, // 보낸 데이터 담기
			BindingResult bindingResult, @SessionAttribute(name = "loginMember", required = false) Member loginMember) {
		log.info("orderCreate 실행");
		log.info("받은 데이터: {}", orderListDTO);

		if (bindingResult.hasErrors()) {
			List<String> fieldErrors = bindingResult.getFieldErrors().stream()
					.map(error -> error.getField() + ": " + error.getDefaultMessage()).collect(Collectors.toList());

			return ResponseEntity.badRequest().body(fieldErrors);
		}
		if (loginMember == null)
			return ResponseEntity.badRequest().body(Map.of("message", "로그인이 필요합니다."));

		List<OrderTable> otlist = orderTableService.findByMember(loginMember);
		for (OrderTable ot : otlist) {
			if (ot.getOrder_status().equals("주문서 작성 중"))
				return ResponseEntity.badRequest().body(Map.of("message", "기존에 작성 중인 주문서가 존재하여 해당 페이지로 이동합니다.",
																"orderId", ot.getId()));
		}

		Long id = orderTableService.createOrder(loginMember, orderListDTO);

		if (id != null) {
			log.info("주문서 생성");
			return ResponseEntity.ok(Map.of("orderId", id));
		}
		return ResponseEntity.badRequest().body(Map.of("message", "알 수 없는 에러: 관리자에게 문의하세요."));
	}
	
	// 주문 리스트 조회
	@GetMapping("list")
	public String myOrderList(@SessionAttribute(name = "loginMember", required = false) Member loginMember,
			Model model
			) {
		if(loginMember == null) {
			model.addAttribute("loginForm", new LoginForm());
			return "member/loginForm"; // 로그인하지 않은 상태라면 로그아웃
		}
		log.info("주문 리스트 조회");
		
		List<OrderAndOrderItemsDTO> oaoil = new ArrayList<>(); // 주문과 해당 주문의 아이템 리스트를 담는 DTO의 리스트 생성
				
		List<OrderTable> otlist = orderTableService.findByMember(loginMember); // 회원의 주문 리스트
		
		for(OrderTable ot : otlist) {
			OrderAndOrderItemsDTO oaoi = new OrderAndOrderItemsDTO(); // 주문과 해당 주문의 아이템 리스트를 담는 DTO 객체 생성
			List<OrderItems> orderItemList = orderTableService.findOrderItemsByOrder(ot); // 해당 주문의 아이템 리스트
			oaoi.setOrderTable(ot); // 주문 할당
			
			List<ProductAndImageDTO> productAndImageList = new ArrayList<>(); // 빈 리스트 생성
			
			for(OrderItems oi : orderItemList) {
				ProductAndImageDTO productAndImage = new ProductAndImageDTO();
				ProductImage productImage = productService.findFileByProductId(oi.getProduct()); // 상품 이미지 객체 가져오기
				
				productAndImage.setProductImage(productImage); // 상품 이미지
				productAndImage.setProduct(oi.getProduct()); // 상품
				productAndImage.setQuantity(oi.getQuantity()); // 주문 수량
				
				productAndImageList.add(productAndImage); // 리스트에 값 추가
			}
			
			oaoi.setProductAndImageList(productAndImageList); // 해당 주문의 아이템 리스트 할당
			
			oaoil.add(oaoi); // 리스트에 주문과 해당 주문의 아이템 리스트를 담은 DTO 객체 추가하기
		}
		
		model.addAttribute("orderAndOrderItemsList", oaoil);
		
		List<CartItem> CartItems = cartService.findByMember(loginMember);
		
		List<Long> idList = new ArrayList<>();
		for(CartItem ci : CartItems) idList.add(ci.getProduct().getProductId());
		model.addAttribute("idList", idList);
		
//		for(OrderAndOrderItemsDTO o: oaoil) {
//			log.info("보낼 데이터: {}", o);
//		}
//		
//		for(OrderAndOrderItemsDTO o: oaoil) {
//			log.info("주문 id: {}\n", o.getOrderTable().getId());
//			for(ProductAndImageDTO oi: o.getProductAndImageList()) {
//				log.info("주문한 상품: {}\n", oi.getProduct().getName());
//			}
//		}
		return "order/orderList";
	}
}
