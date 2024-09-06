package com.example.tabitabi.DTO;

import java.util.List;

import com.example.tabitabi.model.order.OrderItems;
import com.example.tabitabi.model.order.OrderTable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class OrderAndOrderItemsDTO {
	private OrderTable orderTable;
	private List<ProductAndImageDTO> ProductAndImageList;
}
