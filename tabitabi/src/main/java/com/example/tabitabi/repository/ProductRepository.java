package com.example.tabitabi.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.tabitabi.model.Product.Product;
import com.example.tabitabi.model.seller.Seller;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

	Page<Product> findByNameContaining(String searchText, Pageable pageable);

	Page<Product> findBySeller(Seller seller, Pageable pageable);
	
	List<Product> findBySellerId(Long sellerId);
	
	List<Product> findByCategory(String category);

	Product findByProductId(Long productId);
}