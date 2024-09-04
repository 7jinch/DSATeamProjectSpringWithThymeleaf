package com.example.tabitabi.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class OrderDTO {
	private Long productId; // 상품 아이디
	private int quantity; // 상품 주문 수량
}
