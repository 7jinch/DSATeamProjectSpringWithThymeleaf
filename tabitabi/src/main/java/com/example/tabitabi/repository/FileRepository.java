package com.example.tabitabi.repository;

import org.springframework.data.jpa.repository.JpaRepository;


import com.example.tabitabi.model.Product.Product;
import com.example.tabitabi.model.Product.ProductImage;

public interface FileRepository extends JpaRepository<ProductImage, Long> {
    ProductImage findByProduct(Product product);

}