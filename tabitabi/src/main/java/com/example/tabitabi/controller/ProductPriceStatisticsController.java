package com.example.tabitabi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.tabitabi.model.Product.ProductPriceStatistics;
import com.example.tabitabi.service.ProductPriceStatisticsService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class ProductPriceStatisticsController {
   @Autowired
    private ProductPriceStatisticsService service;

   @GetMapping("/api/statistics")
    public List<ProductPriceStatistics> getAllProductStatistics() {
	   log.info("getAllProductStatistics 실행");
        return service.getAllProductStatistics();
    }
}
