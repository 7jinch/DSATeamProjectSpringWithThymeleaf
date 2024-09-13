package com.example.tabitabi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tabitabi.model.Product.ProductPriceStatistics;

public interface ProductPriceStatisticsRepository extends JpaRepository<ProductPriceStatistics, Long> {

}
