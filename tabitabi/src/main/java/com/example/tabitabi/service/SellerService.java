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
import com.example.tabitabi.model.seller.Seller;
import com.example.tabitabi.repository.SellerRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SellerService {

	private static final String UPLOAD_DIR = "src/main/resources/static/uploads/";
	private SellerRepository sellerRepository;
	
	@Autowired
	private void setSellerRepository(SellerRepository sellerRepository) {
		this.sellerRepository = sellerRepository;
	}
	
	public void saveSeller(Seller seller) {
        // 회원 정보 저장
		sellerRepository.save(seller);
	}
	
	public Seller findSellerById(Long id) {
        return sellerRepository.findById(id).orElse(null);
    }
	
	public Seller findSellerByEmail(String email) {
		Seller seller = sellerRepository.findByEmail(email);
		return seller;
	}

	public void removeSeller(String email) {
		Seller seller = findSellerByEmail(email);
		Path uploadPath = Paths.get(System.getProperty("user.dir"), UPLOAD_DIR).toAbsolutePath();

		String existingFileName = seller.getFileName();
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
        
		sellerRepository.deleteById(seller.getId());
	}

	public void updateSeller(Seller seller) {
		Seller findSeller = findSellerByEmail(seller.getEmail());
		log.info("member:{}",findSeller);
		if(seller.getPassword().trim()!="" && seller.getPassword()!= null) {
			findSeller.setPassword(seller.getPassword());
		}
		if(seller.getName().trim()!="" &&seller.getName()!=null) {
			findSeller.setName(seller.getName());
		}
		findSeller.setAddress(seller.getAddress());
		findSeller.setQuestion(seller.getQuestion());
		findSeller.setAnswer(seller.getAnswer());
		
		sellerRepository.save(findSeller);
	}
	
    @Transactional
    public String updateProfile(MultipartFile profile, Seller seller) throws Exception {
        // 파일 저장 경로 설정
        Path uploadPath = Paths.get(System.getProperty("user.dir"), UPLOAD_DIR).toAbsolutePath();

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        String existingFileName = seller.getFileName();
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

        String filename = System.currentTimeMillis() + "_" + profile.getOriginalFilename();
        Path filePath = uploadPath.resolve(filename);

        try {
            profile.transferTo(filePath.toFile());
            seller.setFileName(filename);
            sellerRepository.save(seller);

            // 저장된 이미지의 상대 경로를 반환
            return "/uploads/" + filename;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("파일 저장에 실패했습니다.", e);
        }
    }
    
    public Seller findSeller(String email) {
    	Seller seller = sellerRepository.findByEmail(email);
    	return seller;
    }
    
    public Seller getSellerById(Long id) {
        return sellerRepository.findById(id).orElseThrow(() -> new RuntimeException("Seller not found"));
    }

    public void updateSellerIntro(Seller seller) {
        Seller existingSeller = sellerRepository.findById(seller.getId())
                .orElseThrow(() -> new RuntimeException("Seller not found"));
        existingSeller.setDescription(seller.getDescription());
        sellerRepository.save(existingSeller);
    }
}
