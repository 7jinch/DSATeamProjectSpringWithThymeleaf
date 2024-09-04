package com.example.tabitabi.service;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import com.example.tabitabi.DTO.AddressCreateDTO;
import com.example.tabitabi.DTO.AddressRemoveDTO;
import com.example.tabitabi.model.member.Member;
import com.example.tabitabi.model.orderAddress.OrderAddress;
import com.example.tabitabi.repository.CartItemRepository;
import com.example.tabitabi.repository.CartRepository;
import com.example.tabitabi.repository.OrderAddressRespository;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderAddressService {
	private final OrderAddressRespository addressRespository;

	@Transactional
	public Boolean craeteAddress(Member loginMember, @Valid AddressCreateDTO addressDto) {
		OrderAddress oa = new OrderAddress();
		oa.setAddress(addressDto.getAddress());
		oa.setMember(loginMember);
		addressRespository.save(oa);
		return true;
	}

	public List<OrderAddress> findbyMember(Member findMember) {
		List<OrderAddress> addressList = addressRespository.findByMember(findMember);
		return addressList;
	}

	public List<OrderAddress> findByMember(Member loginMember) {
		List<OrderAddress> addressList = addressRespository.findByMember(loginMember);
		return addressList;
	}

	@Transactional
	public Boolean removeAddress(Long id, @Valid AddressRemoveDTO addressDto) {
		addressRespository.deleteByMemberIdAndAddressId(id, addressDto.getAddressId());
		return true;
	}
}
