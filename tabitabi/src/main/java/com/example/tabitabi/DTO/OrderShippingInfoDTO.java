package com.example.tabitabi.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class OrderShippingInfoDTO {
	private String shipping_name;
	private String shipping_phone_number;
	private String shipping_address;
	private String request_info;
}
