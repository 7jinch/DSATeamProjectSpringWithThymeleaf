package com.example.tabitabi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.tabitabi.model.Product.Product;
import com.example.tabitabi.model.member.Member;
import com.example.tabitabi.model.seller.Seller;
import com.example.tabitabi.model.wishList.Sellerwishlist;
import com.example.tabitabi.model.wishList.WishList;
import com.example.tabitabi.repository.MemberRepository;
import com.example.tabitabi.repository.ProductRepository;
import com.example.tabitabi.repository.SellerRepository;
import com.example.tabitabi.repository.SellerWishListRepository;
import com.example.tabitabi.repository.WishListRepository;

import jakarta.transaction.Transactional;

@Service
public class WishlistService {
		@Autowired
	    private WishListRepository wishlistRepository;

	    @Autowired
	    private MemberRepository memberRepository;

	    @Autowired
	    private ProductRepository productRepository;
	    
	    @Autowired
	    private SellerRepository sellerRepository;

	    @Autowired
	    private SellerWishListRepository sellerWishListRepository;
	    
	    public List<Object[]> getProductsOrderedByWishCount() {
	        return wishlistRepository.findAllByOrderByWishCountDesc();
	    }

	    public boolean isProductInWishlist(Long memberId, Long productId) {
	        Member member = memberRepository.findById(memberId).orElseThrow();
	        Product product = productRepository.findById(productId).orElseThrow();
	        return wishlistRepository.existsByMemberAndProduct(member, product);
	    }
	    @Transactional
	    public String addProductToWishlist(Long memberId, Long productId) {
	        try {
	            Member member = memberRepository.findById(memberId).orElseThrow();
	            Product product = productRepository.findById(productId).orElseThrow();
	            
	            
	            System.out.println("Adding to wishlist - Member ID: " + memberId + ", Product ID: " + productId);
	            if (!wishlistRepository.existsByMemberAndProduct(member, product)) {
	                WishList wishlist = new WishList();
	                wishlist.setMember(member);
	                wishlist.setProduct(product);
	                wishlistRepository.save(wishlist);
	                return "ok";
	            } else {
	                return "already_in_wishlist";
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	            return "fail";
	        }
	    }
	    @Transactional
	    public String removeProductFromWishlist(Long memberId, Long productId) {
	        try {
	            Member member = memberRepository.findById(memberId).orElseThrow();
	            Product product = productRepository.findById(productId).orElseThrow();

	            if (wishlistRepository.existsByMemberAndProduct(member, product)) {
	                wishlistRepository.deleteByMemberAndProduct(member, product);
	                return "ok";
	            } else {
	                return "not_in_wishlist";
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	            return "fail";
	        }
	    }
		public long getWishlistCount(Long productId) {
			return wishlistRepository.countByProductId(productId);
		}
	    
		// 판매자 찜 여부 확인
	    public boolean isSellerInWishlist(Long memberId, Long sellerId) {
	        Member member = memberRepository.findById(memberId).orElseThrow();
	        Seller seller = sellerRepository.findById(sellerId).orElseThrow();
	        return sellerWishListRepository.existsByMemberAndSeller(member, seller);
	    }

	    // 판매자 찜 추가 (본인 판매자 페이지 찜 방지)
	    @Transactional
	    public String addSellerToWishlist(Long memberId, Long sellerId) {
	        try {
	            Member member = memberRepository.findById(memberId).orElseThrow();
	            Seller seller = sellerRepository.findById(sellerId).orElseThrow();

	            
	            if (!sellerWishListRepository.existsByMemberAndSeller(member, seller)) {
	                Sellerwishlist sellerWishlist = new Sellerwishlist();
	                sellerWishlist.setMember(member);
	                sellerWishlist.setSeller(seller);
	                sellerWishListRepository.save(sellerWishlist);
	                return "ok";
	            } else {
	                return "already_in_wishlist";
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	            return "fail";
	        }
	    }

	    // 판매자 찜 삭제
	    @Transactional
	    public String removeSellerFromWishlist(Long memberId, Long sellerId) {
	        try {
	            Member member = memberRepository.findById(memberId).orElseThrow();
	            Seller seller = sellerRepository.findById(sellerId).orElseThrow();

	            if (sellerWishListRepository.existsByMemberAndSeller(member, seller)) {
	                sellerWishListRepository.deleteByMemberAndSeller(member, seller);
	                return "ok";
	            } else {
	                return "not_in_wishlist";
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	            return "fail";
	        }
	    }

	    public long getSellerWishlistCount(Long sellerId) {
	        return sellerWishListRepository.countBySellerId(sellerId);
	    }
	    
	    
	    // 판매자를 찜하고 있는 멤버 수
	    public long getFollowerCountBySeller(Seller seller) {
	        return sellerWishListRepository.countBySeller(seller);
	    }
}
