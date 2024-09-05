package com.example.tabitabi.DTO;

import com.example.tabitabi.model.Product.Product;
import com.example.tabitabi.model.Product.ProductImage;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ProductAndImageDTO {
	private Product product;
	private ProductImage productImage;
	private Integer quantity;
}
