package com.example.tabitabi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tabitabi.model.cart.Cart;
import com.example.tabitabi.model.member.Member;

public interface CartRepository extends JpaRepository<Cart, Long> {

	void save(Member member);

	Cart findByMemberId(Long member_id);

}
