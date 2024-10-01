package com.example.tabitabi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.tabitabi.model.Product.Product;
import com.example.tabitabi.model.member.Member;
import com.example.tabitabi.model.wishList.WishList;



public interface WishListRepository extends JpaRepository<WishList, Long>{
	
	boolean existsByMemberAndProduct(Member member, Product product);
	
    void deleteByMemberAndProduct(Member member, Product product);
    
    @Query("SELECT COUNT(w) FROM WishList w WHERE w.product.productId = :productId")
    long countByProductId(@Param("productId") Long productId);
    
    @Query("SELECT p FROM Product p JOIN WishList w ON p.id = w.product.id GROUP BY p.id ORDER BY COUNT(w.id) DESC")
    List<Object[]> findAllByOrderByWishCountDesc();
	
}
