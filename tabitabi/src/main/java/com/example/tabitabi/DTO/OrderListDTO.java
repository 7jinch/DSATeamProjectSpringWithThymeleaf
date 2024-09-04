package com.example.tabitabi.DTO;

import java.util.List;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter@Setter
@ToString
@NoArgsConstructor
public class OrderListDTO {
	private List<OrderDTO> orders; // 상품 아이디와 주문 수량을 담을 DTO의 리스트
}
