package com.example.tabitabi.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.tabitabi.DTO.OrderDTO;
import com.example.tabitabi.DTO.OrderListDTO;
import com.example.tabitabi.model.Product.Product;
import com.example.tabitabi.model.member.Member;
import com.example.tabitabi.model.order.OrderItems;
import com.example.tabitabi.model.order.OrderTable;
import com.example.tabitabi.model.seller.Seller;
import com.example.tabitabi.repository.MemberRepository;
import com.example.tabitabi.repository.OrderItemsRepository;
import com.example.tabitabi.repository.OrderRepository;

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
	
	public Long createOrder(Member member, @Valid OrderListDTO orderListDTO) {
		OrderTable ot = new OrderTable();
		ot.setMember(member); // 주문한 회원
		ot.setOrder_date(LocalDate.now()); // 주문일
		ot.setOrder_status("주문서 작성 중"); // 주문 상태
		orderRepository.save(ot);
		
		int totalPrice = 0;
		List<OrderDTO> orderDTOList = orderListDTO.getOrders();
		for(OrderDTO od : orderDTOList) {
			OrderItems oi = new OrderItems();
			Product product = productService.findProductById(od.getProductId());
			oi.setProduct(product); // 주문한 상품
			Seller seller = product.getSeller();
			oi.setSeller(seller); // 상품 판매자
			oi.setQuantity(od.getQuantity()); // 각 상품의 수량
			oi.setOrderTable(ot); // 주문
			
			orderItemsRepository.save(oi);
			
			totalPrice += product.getPrice() * od.getQuantity();
		}
		ot.setTotal_price(totalPrice); // 총 가격
		
		orderRepository.save(ot);
		
		Long id = ot.getId();
		
		return id;
	}

	public List<OrderItems> findOrderItemsByOrder(OrderTable findOrder) {
		List<OrderItems> orderItemList = orderItemsRepository.getAllByOrderTable(findOrder);
		return orderItemList;
	}

	public OrderTable findById(Long orderId) {
		OrderTable ot = orderRepository.getById(orderId);
		return ot;
	}

	public List<OrderTable> findByMember(Member loginMember) {
		List<OrderTable> otlist = orderRepository.getByMember(loginMember);
		return otlist;
	}
}
