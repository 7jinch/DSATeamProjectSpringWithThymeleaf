package com.example.tabitabi.model.Product;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class ProductWriteForm {
	@NotBlank
    private String name;
    @NotBlank
    private String description;
    private Integer price;
    private Integer stock;
    private String category;

    private ProductStatus status;
    
    public static Product toProduct(ProductWriteForm productWriteForm) {
    	Product product = new Product();
   
    	product.setName(productWriteForm.getName());
    	product.setDescription(productWriteForm.getDescription());
    	product.setPrice(productWriteForm.getPrice());
    	product.setStock(productWriteForm.getStock());
    	product.setCategory(productWriteForm.getCategory());
    	product.setStatus(productWriteForm.getStatus());
    	
    	return product;
    }
}
