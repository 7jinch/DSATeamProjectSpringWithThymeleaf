package com.example.tabitabi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.tabitabi.model.order.OrderShippingInfo;

@Repository
public interface OrderShippingInfoRespository extends JpaRepository<OrderShippingInfo, Long>{

}
