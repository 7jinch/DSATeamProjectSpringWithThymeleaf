package com.example.tabitabi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.tabitabi.model.member.Member;
import com.example.tabitabi.model.orderAddress.OrderAddress;

public interface OrderAddressRespository extends JpaRepository<OrderAddress, Long> {

	List<OrderAddress> findByMember(Member findMember);

	@Modifying
	@Query("DELETE FROM OrderAddress od WHERE od.member.id = :memberId AND od.id = :addressId")
	void deleteByMemberIdAndAddressId(@Param("memberId") Long memberId, @Param("addressId") Long addressId);
}
