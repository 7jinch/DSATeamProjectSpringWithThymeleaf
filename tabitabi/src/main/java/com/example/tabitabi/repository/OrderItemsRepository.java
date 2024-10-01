package com.example.tabitabi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.tabitabi.model.Product.Product;
import com.example.tabitabi.model.order.OrderItems;
import com.example.tabitabi.model.order.OrderTable;

public interface OrderItemsRepository extends JpaRepository<OrderItems, Long> {

	List<OrderItems> getAllByOrderTable(OrderTable findOrder);

	@Modifying
	@Transactional
	@Query("DELETE FROM OrderItems oi WHERE oi.orderTable.id = :orderId AND oi.product.productId = :productId")
	void deleteByOrderIdAndProductId(@Param("orderId") Long orderId, @Param("productId") Long productId);

	@Query("SELECT oi FROM OrderItems oi WHERE oi.orderTable.id = :orderId AND oi.product.productId = :productId")
	OrderItems findByOrderTableIdAndProductId(@Param("orderId") Long orderId,  @Param("productId") Long productId);

	List<OrderItems> findByOrderTableId(Long orderId);

	@Query("SELECT oi.product, SUM(oi.quantity) AS totalQuantity " +
	           "FROM OrderItems oi " +
	           "GROUP BY oi.product " +
	           "ORDER BY totalQuantity DESC")
	 List<Object[]> findTop10ProductsByOrderQuantity();
}
