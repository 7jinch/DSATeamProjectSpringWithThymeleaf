package com.example.tabitabi.model.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import com.example.tabitabi.model.seller.Seller;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;
    
    @ManyToOne
	@JoinColumn(name = "seller_id")
    private Seller seller;
    
    private String name;
    private String description;
    private Integer price;
    private Integer stock;
    private String category;

    @Enumerated(EnumType.STRING)
    public ProductStatus status;
    
    private Integer HeartNumber;
   
	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductImage> images;
	
	public static ProductUpdateForm toUpdateForm(Product product) {
		ProductUpdateForm productUpdateForm = new ProductUpdateForm();
		productUpdateForm.setProductId(product.getProductId());
		productUpdateForm.setSeller(product.getSeller());
		productUpdateForm.setName(product.getName());
		productUpdateForm.setDescription(product.getDescription());
		productUpdateForm.setPrice(product.getPrice());
		productUpdateForm.setCategory(product.getCategory());
		productUpdateForm.setStock(product.getStock());
		productUpdateForm.setStatus(product.getStatus());
		return productUpdateForm;
	}
	
	 @Override
	    public String toString() {
	        return "Product{" +
	               "productId=" + productId +
	               ", name='" + name + '\'' +
	               ", description='" + description + '\'' +
	               ", price=" + price +
	               ", stock=" + stock +
	               ", category='" + category + '\'' +
	               ", status=" + status +
	               '}';
	    }
}
