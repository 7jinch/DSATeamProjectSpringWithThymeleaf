package com.example.tabitabi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tabitabi.model.Product.ProductPriceStatistics;

public interface ProductPriceStatisticsRepository extends JpaRepository<ProductPriceStatistics, Long> {
   List<ProductPriceStatistics> findAllByOrderByDateAsc();

   // 중복된 데이터가 있는지 확인하는 메서드
    boolean existsByCategoryAndItemAndTypeAndDateAndUnit(String category, String item, String type, String date, String unit);
}
