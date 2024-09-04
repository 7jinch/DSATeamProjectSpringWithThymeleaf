package com.example.tabitabi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tabitabi.model.orderAddress.ShippingAddress;


public interface ShippingAddressRepository extends JpaRepository<ShippingAddress, Long> {

}
