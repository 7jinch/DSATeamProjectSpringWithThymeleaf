package com.example.tabitabi.model.Product;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class ProductPriceStatistics {
   
   @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String category;
    private String item;
    private String type;
    private String date;
    private Double averagePrice;
    private Double maxPrice;
    private Double minPrice;
    private String unit;
}
