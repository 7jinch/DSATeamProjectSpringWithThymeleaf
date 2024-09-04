package com.example.tabitabi.model.member;

import java.time.LocalDate;

import com.example.tabitabi.model.Question;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class MemberJoinForm {
	
	private String email;
	
	private String password;
	
	private String name;
	
	private GenderType gender;
	
	private LocalDate birth;//생년월일
	
	private Question question;
	
	private String answer;
	
	private String phoneNumber;
	

	public static Member toMember(MemberJoinForm memberJoinForm) {
		Member member = new Member();
		member.setEmail(memberJoinForm.getEmail());
		member.setPassword(memberJoinForm.getPassword());
		member.setName(memberJoinForm.getName());
		member.setGender(memberJoinForm.getGender());
		member.setBirth(memberJoinForm.getBirth());
		member.setQuestion(memberJoinForm.getQuestion());
		member.setAnswer(memberJoinForm.getAnswer());
		member.setPhoneNumber(memberJoinForm.getPhoneNumber());
		
		return member;
	}
}
