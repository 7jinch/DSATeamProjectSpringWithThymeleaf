package com.example.tabitabi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.tabitabi.model.member.Member;
import com.example.tabitabi.model.seller.Seller;
import com.example.tabitabi.model.wishList.Sellerwishlist;

public interface SellerWishListRepository extends JpaRepository<Sellerwishlist, Long> {

    boolean existsByMemberAndSeller(Member member, Seller seller);

    void deleteByMemberAndSeller(Member member, Seller seller);

    long countBySellerId(Long sellerId);
    
    long countBySeller(Seller seller);
}