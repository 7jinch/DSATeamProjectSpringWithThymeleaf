package com.example.tabitabi.model.member;

import java.time.LocalDate;

import com.example.tabitabi.model.Question;
import com.example.tabitabi.model.cart.Cart;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	//GenerationType.IDENTITY -> id값(PK)이 있어야함, 자동증가되니 걱정 ㄴㄴ
	private Long id;
	
	@Column(nullable = false, unique = true)
	//unique를 줌으로써 email은 중복값 받을 수 없게-> 이메일이 Id를 대체
	private String email;
	
	@Column(nullable = false)
	private String password;
	
	@Column(nullable = false)
	private String name;
	
	@Enumerated(EnumType.STRING)
	private GenderType gender;
	
	@Column(nullable = false)
	private LocalDate birth;
	
	@Column(nullable = false)
	private String phoneNumber;
	
	@Nullable
	private String address;

	private String fileName;

	private Question question;
	
	private String answer;
}
