package com.example.tabitabi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.tabitabi.service.WishlistService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @PostMapping("/addWishlist")
    @ResponseBody
    public String addWishlist(@RequestParam(name = "memberId") Long memberId, @RequestParam(name = "productId") Long productId) {
    	String result = wishlistService.addProductToWishlist(memberId, productId);
        
        return result;     
    }

    @PostMapping("/removeWishlist")
    @ResponseBody
    public String removeWishlist(@RequestParam(name = "memberId") Long memberId, @RequestParam(name = "productId") Long productId) {
        return wishlistService.removeProductFromWishlist(memberId, productId);
    }
    
    @GetMapping("/isInWishlist")
    public boolean isInWishlist(@RequestParam(name = "memberId") Long memberId, @RequestParam(name = "productId") Long productId) {
        return wishlistService.isProductInWishlist(memberId, productId);
    }
    
    @PostMapping("/addSellerToWishlist")
    @ResponseBody
    public String addSellerToWishlist(@RequestParam(name = "memberId") Long memberId, @RequestParam(name = "sellerId") Long sellerId) {
    	String result = wishlistService.addSellerToWishlist(memberId, sellerId);
        
        return result;
    }

    @PostMapping("/removeSellerFromWishlist")
    @ResponseBody
    public String removeSellerFromWishlist(@RequestParam(name = "memberId") Long memberId, @RequestParam(name = "sellerId") Long sellerId) {
        return wishlistService.removeSellerFromWishlist(memberId, sellerId);
    }

    @GetMapping("/isSellerInWishlist")
    public boolean isSellerInWishlist(@RequestParam(name = "memberId") Long memberId, @RequestParam(name = "sellerId") Long sellerId) {
        return wishlistService.isSellerInWishlist(memberId, sellerId);
    }
}
