package com.example.tabitabi.model.Product;

import java.math.BigDecimal;

import com.example.tabitabi.model.seller.Seller;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class ProductUpdateForm {
	
    private Long productId;
    
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    private Integer price;
    private Integer stock;
    private String category;
    private boolean fileRemoved;

    public ProductStatus status;
    public Seller seller;
    
    public static Product toProduct(ProductUpdateForm productUpdateForm) {
    	Product product = new Product();
    	product.setProductId(productUpdateForm.getProductId());
    	product.setName(productUpdateForm.getName());
    	product.setDescription(productUpdateForm.getDescription());
    	product.setPrice(productUpdateForm.getPrice());
    	product.setStock(productUpdateForm.getStock());
    	product.setCategory(productUpdateForm.getCategory());
    	product.setStatus(productUpdateForm.getStatus());
    	
    	return product;
    }

}
