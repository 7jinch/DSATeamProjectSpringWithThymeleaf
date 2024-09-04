package com.example.tabitabi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tabitabi.model.member.Member;
import com.example.tabitabi.model.seller.Seller;

public interface SellerRepository extends JpaRepository<Seller, Long>{
	Seller findByEmail(String email);
}
