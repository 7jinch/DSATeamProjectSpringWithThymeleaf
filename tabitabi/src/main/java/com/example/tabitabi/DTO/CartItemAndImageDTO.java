package com.example.tabitabi.DTO;

import java.util.List;

import com.example.tabitabi.model.Product.ProductImage;
import com.example.tabitabi.model.cart.CartItem;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CartItemAndImageDTO {
	private ProductImage productImage;
	private CartItem cartItem;
}
