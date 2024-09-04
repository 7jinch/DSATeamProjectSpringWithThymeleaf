package com.example.tabitabi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tabitabi.model.member.Member;

public interface MemberRepository extends JpaRepository<Member, Long>{
	 Member findByEmail(String email);
}
