package com.example.tabitabi.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.tabitabi.model.member.Member;
import com.example.tabitabi.repository.MemberRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MemberService {
	
	private static final String UPLOAD_DIR = "src/main/resources/static/uploads/";
	private MemberRepository memberRepository;
	
	@Autowired
	private void setMemberRepository(MemberRepository memberRepository) {
		this.memberRepository=memberRepository;
	}
	
	public void saveMember(Member member) {
		memberRepository.save(member);
	}

	public Member findMemberByEmail(String email) {
		Member member = memberRepository.findByEmail(email);
		return member;
	}
	
	public void removeMember(String email) {
		Member member = findMemberByEmail(email);
		Path uploadPath = Paths.get(System.getProperty("user.dir"), UPLOAD_DIR).toAbsolutePath();

		String existingFileName = member.getFileName();
        if (existingFileName != null && !existingFileName.isEmpty()) {
            Path existingFilePath = uploadPath.resolve(existingFileName);
            if (Files.exists(existingFilePath)) {
                try {
                    Files.delete(existingFilePath);
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException("기존 파일 삭제에 실패했습니다.", e);
                }
            }
        }
		memberRepository.deleteById(member.getId());
	}

	public void updateMember(Member member) {
		Member findMember = findMemberByEmail(member.getEmail());
		log.info("member:{}",member);
		if(member.getPassword().trim()!="" && member.getPassword()!= null) {
			findMember.setPassword(member.getPassword());
		}
		if(member.getName().trim()!="" &&member.getName()!=null) {
			findMember.setName(member.getName());
		}
		findMember.setAddress(member.getAddress());
		findMember.setQuestion(member.getQuestion());
		findMember.setAnswer(member.getAnswer());
		
		memberRepository.save(findMember);
	}
	
    @Transactional
    public String updateProfile(MultipartFile profile, Member member) throws Exception {
        // 파일 저장 경로 설정
        Path uploadPath = Paths.get(System.getProperty("user.dir"), UPLOAD_DIR).toAbsolutePath();

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        // 이거 안 하면 이미지가 계속 새로 추가됌
        //안 해도 상관없지만 하지 않을 경우 이미지가 너무 많아질 것 같아 추가함
        String existingFileName = member.getFileName();
        if (existingFileName != null && !existingFileName.isEmpty()) {
            Path existingFilePath = uploadPath.resolve(existingFileName);
            if (Files.exists(existingFilePath)) {
                try {
                    Files.delete(existingFilePath);
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException("기존 파일 삭제에 실패했습니다.", e);
                }
            }
        }

        // 파일 이름 설정 (중복 방지를 위해 현재 시간 사용)
        String filename = System.currentTimeMillis() + "_" + profile.getOriginalFilename();
        Path filePath = uploadPath.resolve(filename);

        try {
            // 파일을 지정된 경로에 저장
            profile.transferTo(filePath.toFile());

            // 저장된 파일 경로를 멤버의 프로필 이미지로 설정
            member.setFileName(filename); // 여기서는 파일명만 저장

            // 데이터베이스에 업데이트
            memberRepository.save(member);

            // 저장된 이미지의 상대 경로를 반환
            return "/uploads/" + filename;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("파일 저장에 실패했습니다.", e);
        }
    }

	public Member findMemberById(Long member_id) {
		Member member = memberRepository.getById(member_id);
		return member;
	}
}
