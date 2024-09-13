package com.example.tabitabi.model.orderAddress;

import com.example.tabitabi.model.order.OrderTable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class ShippingAddress { // 판매자 판매, 배송 가능 지역
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@OneToOne
	private OrderTable orderTable;
	
	private String name;
	private String phone_number;
	private String shippingAddres;
	private String requestDetails;
}
