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

import com.example.tabitabi.DTO.OrderListDTO;
import com.example.tabitabi.DTO.ProductAndImageDTO;
import com.example.tabitabi.model.Product.ProductImage;
import com.example.tabitabi.model.member.Member;
import com.example.tabitabi.model.order.OrderItems;
import com.example.tabitabi.model.order.OrderTable;
import com.example.tabitabi.model.orderAddress.OrderAddress;
import com.example.tabitabi.model.seller.LoginForm;
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

	@GetMapping("{orderId}")
	public String orderPage(@PathVariable(name = "orderId") Long orderId, Model model,
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

	@PostMapping("")
	public ResponseEntity<?> orderCreate(@Valid @RequestBody OrderListDTO orderListDTO, // 보낸 데이터 담기
			BindingResult bindingResult, @SessionAttribute(name = "loginMember", required = false) Member loginMember) {
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
	
	@GetMapping("list")
	public String myOrderList(@SessionAttribute(name = "loginMember", required = false) Member loginMember,
			Model model
			) {
		if(loginMember == null) {
			model.addAttribute("loginForm", new LoginForm());
			return "member/loginForm"; // 로그인하지 않은 상태라면 로그아웃
		}
		
		List<OrderTable> otlist = orderTableService.findByMember(loginMember);
		
		model.addAttribute("orderTableList", otlist);
		return "order/orderList";
	}
}
