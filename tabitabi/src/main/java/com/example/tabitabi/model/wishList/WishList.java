package com.example.tabitabi.model.wishList;

import java.time.LocalDate;

import com.example.tabitabi.model.Product.Product;
import com.example.tabitabi.model.member.GenderType;
import com.example.tabitabi.model.member.Member;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
public class WishList {
	 @Id
	 @GeneratedValue(strategy = GenerationType.IDENTITY)
	 private Long id;

	 @ManyToOne(fetch = FetchType.LAZY)
	 @JoinColumn(name = "member_id")
	 private Member member;

	 @ManyToOne(fetch = FetchType.LAZY)
	 @JoinColumn(name = "product_id")
	 private Product product;
}
