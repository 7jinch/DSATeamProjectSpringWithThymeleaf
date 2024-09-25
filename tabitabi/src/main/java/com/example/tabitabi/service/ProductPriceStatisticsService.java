package com.example.tabitabi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.tabitabi.model.Product.ProductPriceStatistics;
import com.example.tabitabi.repository.ProductPriceStatisticsRepository;

@Service
public class ProductPriceStatisticsService {
   @Autowired
    private ProductPriceStatisticsRepository repository;

   public List<ProductPriceStatistics> getAllProductStatistics() {
        return repository.findAllByOrderByDateAsc();
    }
}
