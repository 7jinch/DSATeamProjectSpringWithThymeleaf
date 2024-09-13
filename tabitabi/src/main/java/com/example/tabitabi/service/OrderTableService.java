package com.example.tabitabi.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.tabitabi.DTO.OrderDTO;
import com.example.tabitabi.DTO.OrderListDTO;
import com.example.tabitabi.DTO.OrderShippingInfoDTO;
import com.example.tabitabi.model.Product.Product;
import com.example.tabitabi.model.Product.ProductStatus;
import com.example.tabitabi.model.cart.CartItem;
import com.example.tabitabi.model.member.Member;
import com.example.tabitabi.model.order.OrderItems;
import com.example.tabitabi.model.order.OrderShippingInfo;
import com.example.tabitabi.model.order.OrderTable;
import com.example.tabitabi.model.seller.Seller;
import com.example.tabitabi.repository.CartItemRepository;
import com.example.tabitabi.repository.CartRepository;
import com.example.tabitabi.repository.MemberRepository;
import com.example.tabitabi.repository.OrderItemsRepository;
import com.example.tabitabi.repository.OrderRepository;
import com.example.tabitabi.repository.OrderShippingInfoRespository;
import com.example.tabitabi.repository.ProductRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderTableService {
	private final OrderRepository orderRepository;
	private final OrderItemsRepository orderItemsRepository;
	private final ProductService productService;
	private final MemberService memberService;
	private final CartRepository cartRepository;
	private final CartItemRepository cartItemRepository;
	private final OrderShippingInfoRespository orderShippingInfoRespository;
	private final ProductRepository productRepository;
	
	// 주문 생성
	@Transactional
	public Long createOrder(Member member, @Valid OrderListDTO orderListDTO) {
		OrderTable ot = new OrderTable();
		ot.setMember(member); // 주문한 회원
		ot.setOrder_date(LocalDate.now()); // 주문일
		ot.setOrder_status("주문서 작성 중"); // 주문 상태
		orderRepository.save(ot);
		
		int totalPrice = 0;
		List<OrderDTO> orderDTOList = orderListDTO.getOrders(); // 주문에 추가용
		List<Long> productIdList = new ArrayList<>(); // 장바구니에서 삭제용
		
		for(OrderDTO od : orderDTOList) {
			// 주문에 상품 추가하기
			OrderItems oi = new OrderItems();
			Product product = productService.findProductById(od.getProductId());
			oi.setProduct(product); // 주문한 상품
			Seller seller = product.getSeller();
			oi.setSeller(seller); // 상품 판매자
			oi.setQuantity(od.getQuantity()); // 각 상품의 수량
			oi.setOrderTable(ot); // 주문
			
			orderItemsRepository.save(oi);
			
			totalPrice += product.getPrice() * od.getQuantity();
			
			// 장바구니에서 주문한 상품 삭제하려고 상품 id를 리스트에 추가
			productIdList.add(od.getProductId());
			
		}
		ot.setTotal_price(totalPrice); // 총 가격
		
		orderRepository.save(ot); // 주문 생성
		
		// 주문한 상품은 장바구니에서 삭제
		Long cartId = cartRepository.getById(member.getId()).getId();
		cartItemRepository.deleteByCartIdAndProductIds(cartId, productIdList);
		
		// 상품 수량 수정
		for(OrderDTO od : orderDTOList) {
			Product product = productService.findProductById(od.getProductId());
			int newStock = product.getStock() - od.getQuantity();
			product.setStock(newStock);
			
			if(newStock <= 0) product.setStatus(ProductStatus.SOLD_OUT);
			
			productRepository.save(product);
		}
		
		Long id = ot.getId();
		
		return id;
	}

	// 해당 주문의 상품들을 리스트로 반환
	public List<OrderItems> findOrderItemsByOrder(OrderTable findOrder) {
		List<OrderItems> orderItemList = orderItemsRepository.getAllByOrderTable(findOrder);
		return orderItemList;
	}

	// 주문 조회
	public OrderTable findById(Long orderId) {
		OrderTable ot = orderRepository.getById(orderId);
		return ot;
	}

	// 회원별 주문을 리스트로 반환
	public List<OrderTable> findByMember(Member loginMember) {
		List<OrderTable> otlist = orderRepository.getByMember(loginMember);
		return otlist;
	}
	
	// 배송지 정보 생성
	@Transactional
	public void createShippingInfo(Long orderId, @Valid OrderShippingInfoDTO orderShippingInfoDTO) {
		OrderTable ot = orderRepository.getById(orderId);
		if(ot.getOrderShippingInfo() == null) return; // 이미 배송지 주소를 설정했으면

		// 배송지 정보 저장
		OrderShippingInfo osi = new OrderShippingInfo();
		osi.setShipping_name(orderShippingInfoDTO.getShipping_name());
		osi.setShipping_phone_number(orderShippingInfoDTO.getShipping_phone_number());
		osi.setShipping_address(orderShippingInfoDTO.getShipping_address());
		osi.setRequest_info(orderShippingInfoDTO.getRequest_info());
		OrderShippingInfo newOsi = orderShippingInfoRespository.save(osi);
		
		// 주문에 배송지 정보 설정
		ot.setOrderShippingInfo(newOsi);
		orderRepository.save(ot);
		
	}

	// 주문서 작성 완료(배송지 작성)
	@Transactional
	public void settingOrderStatusAfterShippingInfo(Long orderId) {
		OrderTable ot = orderRepository.getById(orderId);
		ot.setOrder_status("주문 완료 - 판매자가 주문을 확인하는 중");
		orderRepository.save(ot);
	}

	public List<OrderItems> findOrderItemsByOrder(Long orderId) {
		List<OrderItems> orderItemList = orderItemsRepository.getAllByOrderTable(orderRepository.getById(orderId));
		return orderItemList;
	}
}
