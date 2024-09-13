package com.example.tabitabi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.tabitabi.model.Product.Product;
import com.example.tabitabi.model.chat.ChatRoom;
import com.example.tabitabi.model.member.Member;
import com.example.tabitabi.model.seller.Seller;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
	List<ChatRoom> findByMember(Member member);
	List<ChatRoom> findBySeller(Seller seller);
	List<ChatRoom> findByProduct(Product product);
	Optional<ChatRoom> findById(Long id);
	ChatRoom findByProductAndSellerAndMember(Product product,Seller seller,Member member);
}
