package com.example.tabitabi.model.seller;

import java.time.LocalDate;

import com.example.tabitabi.model.Question;
import com.example.tabitabi.model.member.GenderType;
import com.example.tabitabi.model.member.Member;
import com.example.tabitabi.model.member.MemberJoinForm;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class SellerJoinForm {
	
	private String email;
	
	private String password;
	
	private String name;
	
	private GenderType gender;
	
	private LocalDate birth;//생년월일
	
	private Question question;
	
	private String answer;
	
	private String phoneNumber;
	
	private String nickname;

	public static Seller toSeller(SellerJoinForm memberJoinForm) {
		Seller seller = new Seller();
		seller.setEmail(memberJoinForm.getEmail());
		seller.setPassword(memberJoinForm.getPassword());
		seller.setName(memberJoinForm.getName());
		seller.setGender(memberJoinForm.getGender());
		seller.setBirth(memberJoinForm.getBirth());
		seller.setQuestion(memberJoinForm.getQuestion());
		seller.setAnswer(memberJoinForm.getAnswer());
		seller.setPhoneNumber(memberJoinForm.getPhoneNumber());
		seller.setNickname(memberJoinForm.getNickname());
		
		return seller;
	}
	
	public static Member toMember(SellerJoinForm memberJoinForm) {
		Member seller = new Member();
		seller.setEmail(memberJoinForm.getEmail());
		seller.setPassword(memberJoinForm.getPassword());
		seller.setName(memberJoinForm.getName());
		seller.setGender(memberJoinForm.getGender());
		seller.setBirth(memberJoinForm.getBirth());
		seller.setQuestion(memberJoinForm.getQuestion());
		seller.setAnswer(memberJoinForm.getAnswer());
		seller.setPhoneNumber(memberJoinForm.getPhoneNumber());
		seller.setNickname(memberJoinForm.getNickname());
		
		return seller;
	}
}
