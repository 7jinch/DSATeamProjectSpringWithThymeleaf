package com.example.tabitabi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tabitabi.model.order.OrderItems;
import com.example.tabitabi.model.order.OrderTable;

public interface OrderItemsRepository extends JpaRepository<OrderItems, Long> {

	List<OrderItems> getAllByOrderTable(OrderTable findOrder);
}
