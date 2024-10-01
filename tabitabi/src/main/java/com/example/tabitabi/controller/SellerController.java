package com.example.tabitabi.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.tabitabi.model.Question;
import com.example.tabitabi.model.Product.Product;
import com.example.tabitabi.model.Product.ProductImage;
import com.example.tabitabi.model.member.Member;
import com.example.tabitabi.model.seller.LoginForm;
import com.example.tabitabi.model.seller.Seller;
import com.example.tabitabi.model.seller.SellerJoinForm;
import com.example.tabitabi.service.CartService;
import com.example.tabitabi.service.EmailService;
import com.example.tabitabi.service.MemberService;
import com.example.tabitabi.service.ProductService;
import com.example.tabitabi.service.SellerService;
import com.example.tabitabi.service.WishlistService;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("seller")
public class SellerController {

	private SellerService sellerService;

	private MemberService memberService;
	
	private ProductService productService;
	
	private WishlistService wishlistService;

	@Autowired
	private EmailService emailService;

	@Autowired
	public void setSellerService(SellerService sellerService) {
		this.sellerService = sellerService;
	}

	@Autowired
	public void setMemberService(MemberService memberService) {
		this.memberService = memberService;
	}
	
	@Autowired
	public void setwishlistService(WishlistService wishlistService) {
		this.wishlistService = wishlistService;
	}

	@Autowired
	private CartService cartService; // 의존성 주입

	private String generateVerificationCode() {
		// randomUUID는 기본적으로 36자 생성, replace로 문자열에서 원하는 부분 지울 수 있다
		// substring 0번째 글자부터 4번째까지만 받아오기
		return java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 5);
	}

	@GetMapping("join")
	public String joinForm(Model model) {
		log.info("joinForm 실행");
		model.addAttribute("member", new SellerJoinForm());
		model.addAttribute("questions", Question.values());
		return "seller/joinForm";
	}

	@PostMapping("join/emailCode")
	public ResponseEntity<Map<String, String>> emailCode(@RequestParam("email") String email) {
		log.info("emailCode 실행");
		Map<String, String> response = new HashMap<>();
		try {
			String verificationCode = generateVerificationCode();
			emailService.sendVerificationEmail(email, verificationCode);
			response.put("emailCode", verificationCode);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PostMapping("join")
	public ResponseEntity<String> join(@Validated @ModelAttribute("seller") SellerJoinForm sellerJoinForm) {
		log.info("join 실행");
		Seller seller = sellerJoinForm.toSeller(sellerJoinForm);
		
		String sellerEmail = seller.getEmail();
		if(sellerService.findSellerByEmail(sellerEmail) != null) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); 
		}
		
		sellerService.saveSeller(seller);
		if(memberService.findMemberByEmail(sellerEmail) == null) {
			Member member = sellerJoinForm.toMember(sellerJoinForm);
			memberService.saveMember(member);
			cartService.createCart(member); // 장바구니 생성
		}

		return ResponseEntity.status(HttpStatus.OK).body("ok");
	}

	@GetMapping("login")
	public String loginForm(Model model) {
		log.info("loginForm 실행");
		model.addAttribute("loginForm", new LoginForm());
		return "seller/loginForm";
	}

	@PostMapping("login")
	public String login(@Validated @ModelAttribute LoginForm loginForm
			,HttpServletRequest request
			, BindingResult result
            , HttpServletResponse response) {
		Seller findSeller = sellerService.findSellerByEmail(loginForm.getEmail());
		if(result.hasErrors()) {
			return "member/loginForm";
		}
		if(findSeller == null) {
			result.reject("noID", "아이디가 존재 하지 않습니다.");
			return "seller/loginForm";
		}
		if(findSeller.getPassword().equals(loginForm.getPassword())) {
			HttpSession session = request.getSession();
			session.setAttribute("loginSeller", findSeller);

			return "redirect:/";
		}
		result.reject("notPassword", "패스워드가 맞지 않습니다.");
		return "seller/loginForm";
	}

	@GetMapping("logout")
	public String logout(HttpSession session) {
		log.info("logout 실행");
		session.setAttribute("loginSeller", null);

		return "mall";
	}

	// 비번 검사용
	@PostMapping("mypage")
	public ResponseEntity<String> pwcheck(@RequestParam("email") String email,
			@RequestParam("password") String password) {
		log.info("pwcheck 실행");
		if (sellerService.findSellerByEmail(email).getPassword().equals(password)) {
			return ResponseEntity.ok("correct password");
		}
		// 400오류내줌. 확인하시고 싶으시다면 mypage의 pwCheck()함수에서 text오류를 출력
		return ResponseEntity.badRequest().body("password is wrong");
	}

	@GetMapping("mypage")
	public String mypage(HttpSession session, Model model) {
		log.info("mypage 실행");
		Seller seller = (Seller) session.getAttribute("loginSeller");
		Seller findSeller = sellerService.findSellerByEmail(seller.getEmail());
		model.addAttribute("seller", findSeller);
		model.addAttribute("questions", Question.values());
		model.addAttribute("defaultQu", findSeller.getQuestion());
		return "seller/mypage";
	}

	@DeleteMapping("mypage")
	public ResponseEntity<String> removeSeller(@RequestParam("email") String email, HttpServletRequest request) {
		log.info("removeSeller 실행");
		HttpSession session = request.getSession();
		sellerService.removeSeller(email);
		session.invalidate();

		return ResponseEntity.ok("정상삭제");
	}

	@PostMapping("update")
	public String update(@Validated @ModelAttribute Seller seller) {
		log.info("update 실행");
		sellerService.updateSeller(seller);

		return "redirect:/";
	}

	// 이미지 설정
	@PutMapping("mypage")
	public ResponseEntity<String> profileUpdate(@RequestParam("profile") MultipartFile profile, HttpSession session)
			throws Exception {
		log.info("profileUpdate 실행");
		Seller seller = (Seller) session.getAttribute("loginSeller");
		if (profile.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("사진이 전송되지 않았습니다.");
		}
		try {
			String imageUrl = sellerService.updateProfile(profile, seller);
			return ResponseEntity.ok("수정 성공: " + imageUrl);

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("프로필 수정 중 오류가 발생했습니다: " + e.getMessage());
		}
	}

	@GetMapping("forgotPw")
	public String forgot(Model model) {
		log.info("forgot 실행");
		model.addAttribute("questions", Question.values());

		return "forgotPw";
	}

    @PostMapping("forgotPw")
    public ResponseEntity<Object> forgotPassword(HttpSession session
    		,@RequestParam("email") String email
    		,@RequestParam("question") Question question
    		,@RequestParam("answer") String answer) {
    	Seller findSeller = sellerService.findSellerByEmail(email);
    	if(findSeller == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    	}
    	if(findSeller.getQuestion().equals(question) && findSeller.getAnswer().equals(answer)) {
            session.setAttribute("email", email);
    		return ResponseEntity.status(HttpStatus.OK).body(findSeller);
    	}
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @GetMapping("forgotPw/setPw")
    public String setPw(Model model, HttpSession session) {
    	String email = (String) session.getAttribute("email");
    	if(email!= null) {
    		Seller seller = sellerService.findSellerByEmail(email);
    		model.addAttribute("seller",seller);
    		return "setPw";
    	}
		return "redirect:/";
    }

    @PostMapping("forgotPw/setPw")
    public String setPwd(HttpSession session
    		,@RequestParam("password") String password){
    	String email = (String) session.getAttribute("email");
    	if(email != null) {
    		Seller seller = sellerService.findSellerByEmail(email);
    		seller.setPassword(password);
    		sellerService.saveSeller(seller);
    	
    		return "redirect:/";
    	}
    	return "redirect:/";
    }
	
    @PostMapping("join/emailCheck")
    public ResponseEntity<?> emailCheck(@RequestParam("email") String email){
    	Seller seller = sellerService.findSellerByEmail(email);
    	if(seller != null) {
    		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    	}
    	return ResponseEntity.status(HttpStatus.OK).build();
    }
    
    @GetMapping("/{id}/intro")
    public String showIntroForm(@PathVariable("id") Long id, Model model) {
        Seller seller = sellerService.getSellerById(id);
        if (seller != null) {
            model.addAttribute("seller", seller);
            return "seller/introregi"; // 소개글 작성 폼
        } else {
            return "error/404"; // Custom 404 error page
        }
    }

    // 셀러 소개글 수정 처리
    @PostMapping("/introregi")
    public String updateIntro(@ModelAttribute Seller seller, RedirectAttributes redirectAttributes) {
    	if (seller.getId() == null) {
            // 예외 처리 또는 에러 메시지 추가
            return "redirect:/error";
        }
    	sellerService.updateSellerIntro(seller);
        redirectAttributes.addFlashAttribute("message", "소개글이 수정되었습니다.");
        return "redirect:/"; // 수정 후 이동할 페이지
    }
    
    @GetMapping("/{id}/introshow")
    public String showSellerIntro(@PathVariable("id") Long id, Model model,
    		@SessionAttribute(name = "loginMember", required = false) Member loginMember) {
    	Seller seller = sellerService.getSellerById(id);
        List<Product> sellerProducts = productService.getProductsBySellerId(id);
        long followerCount = wishlistService.getFollowerCountBySeller(seller);
        for (Product product : sellerProducts) {
            List<ProductImage> productImages = product.getImages(); // 상품의 이미지 목록 가져오기
            if (!productImages.isEmpty()) {
                product.setImages(productImages); // 상품에 이미지 목록 설정
            }
        }
        
        if (seller != null) {
            model.addAttribute("seller", seller);
            model.addAttribute("sellerProducts", sellerProducts);
            model.addAttribute("followerCount", followerCount);
            boolean isInWishlistS = false;
            boolean isOwnSeller = false;
            if (loginMember != null) {
                Long memberId = loginMember.getId(); // 로그인된 회원의 ID를 가져옴
                String memberemail = loginMember.getEmail();
                isInWishlistS = wishlistService.isProductInWishlist(memberId, id);
                if (seller.getEmail().equals(memberemail)) { // 
                    isOwnSeller = true; //
                }
                model.addAttribute("memberId", memberId); // 추가된 부분: memberId를 모델에 추가
            }
           
            model.addAttribute("isInWishlist", isInWishlistS);
            model.addAttribute("isOwnSeller", isOwnSeller);
            return "seller/intro";
        } else {
            return "error/404";
        }
    }
    
    // 파일을 표시하기 위한 메서드
    @GetMapping("/files")
    @ResponseBody
    public ResponseEntity<Resource> display(@RequestParam("filename") String filename) {
        String path = "C:\\upload\\"; // 파일 경로 설정

        Resource resource = new FileSystemResource(path + filename); // 파일 리소스 생성

        if (!resource.exists()) { // 파일이 없을 경우 에러 반환
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        HttpHeaders header = new HttpHeaders(); // HTTP 헤더 설정
        Path filePath = Paths.get(path + filename);

        try {
            header.add(HttpHeaders.CONTENT_TYPE, Files.probeContentType(filePath)); // 파일의 MIME 타입 설정
        } catch (IOException e) {
            log.error("File type determination failed", e); // MIME 타입 설정 실패 시 에러 로그 출력
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // 에러 반환
        }

        return new ResponseEntity<>(resource, header, HttpStatus.OK); 
    }
}
