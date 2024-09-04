package com.example.tabitabi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tabitabi.model.member.Member;
import com.example.tabitabi.model.order.OrderTable;

public interface OrderRepository extends JpaRepository<OrderTable, Long> {

	List<OrderTable> getByMember(Member member);

}
