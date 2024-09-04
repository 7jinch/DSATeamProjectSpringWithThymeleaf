package com.example.tabitabi.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.tabitabi.model.Question;
import com.example.tabitabi.model.member.Member;
import com.example.tabitabi.model.seller.LoginForm;
import com.example.tabitabi.model.seller.Seller;
import com.example.tabitabi.model.seller.SellerJoinForm;
import com.example.tabitabi.service.CartService;
import com.example.tabitabi.service.EmailService;
import com.example.tabitabi.service.MemberService;
import com.example.tabitabi.service.SellerService;

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
	private CartService cartService; // 의존성 주입

	private String generateVerificationCode() {
		// randomUUID는 기본적으로 36자 생성, replace로 문자열에서 원하는 부분 지울 수 있다
		// substring 0번째 글자부터 4번째까지만 받아오기
		return java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 5);
	}

	@GetMapping("join")
	public String joinForm(Model model) {
		model.addAttribute("member", new SellerJoinForm());
		model.addAttribute("questions", Question.values());
		return "seller/joinForm";
	}

	@PostMapping("join/emailCode")
	public ResponseEntity<Map<String, String>> emailCode(@RequestParam("email") String email) {
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
		Seller seller = sellerJoinForm.toSeller(sellerJoinForm);
		sellerService.saveSeller(seller);
		if (memberService.findMemberByEmail(sellerJoinForm.getEmail()) == null) {
			Member member = sellerJoinForm.toMember(sellerJoinForm);
			memberService.saveMember(member);
			cartService.createCart(member); // 장바구니 생성
		}
		;

		return ResponseEntity.status(HttpStatus.OK).body("ok");
	}

	@GetMapping("login")
	public String loginForm(Model model) {
		model.addAttribute("loginForm", new LoginForm());
		return "seller/loginForm";
	}

	@PostMapping("login")
	public String login(@Validated @ModelAttribute LoginForm loginForm, HttpServletRequest request,
			BindingResult result, HttpServletResponse response, HttpSession session) {
		Seller findSeller = sellerService.findSellerByEmail(loginForm.getEmail());
		if (result.hasErrors()) {
			return "member/loginForm";
		}
		if (findSeller == null) {
			result.reject("noID", "아이디가 존재 하지 않습니다.");
			return "seller/loginForm";
		}
		if (findSeller.getPassword().equals(loginForm.getPassword())) {
			session = request.getSession();
			session.setAttribute("loginSeller", findSeller);

			return "redirect:/";
		}
		if (request.getSession().getAttribute("loginMember") != null
				|| request.getSession().getAttribute("loginSeller") != null) {
			return "redirect:/";
		}
		result.reject("notPassword", "패스워드가 맞지 않습니다.");
		return "seller/loginForm";
	}

	@GetMapping("logout")
	public String logout(HttpSession session) {
		session.setAttribute("loginSeller", null);

		return "redirect:/";
	}

	// 비번 검사용
	@PostMapping("mypage")
	public ResponseEntity<String> pwcheck(@RequestParam("email") String email,
			@RequestParam("password") String password) {
		if (sellerService.findSellerByEmail(email).getPassword().equals(password)) {
			return ResponseEntity.ok("correct password");
		}
		// 400오류내줌. 확인하시고 싶으시다면 mypage의 pwCheck()함수에서 text오류를 출력
		return ResponseEntity.badRequest().body("password is wrong");
	}

	@GetMapping("mypage")
	public String mypage(HttpSession session, Model model) {
		Seller seller = (Seller) session.getAttribute("loginSeller");
		if(seller == null) {
			model.addAttribute("loginForm", new LoginForm());
			return "seller/loginForm";
		}
		Seller findSeller = sellerService.findSellerByEmail(seller.getEmail());
		model.addAttribute("seller", findSeller);
		model.addAttribute("questions", Question.values());
		model.addAttribute("defaultQu", findSeller.getQuestion());
		return "seller/mypage";
	}

	@DeleteMapping("mypage")
	public ResponseEntity<String> removeSeller(@RequestParam("email") String email, HttpServletRequest request) {
		HttpSession session = request.getSession();
		sellerService.removeSeller(email);
		session.invalidate();

		return ResponseEntity.ok("정상삭제");
	}

	@PostMapping("update")
	public String update(@Validated @ModelAttribute Seller seller) {
		sellerService.updateSeller(seller);

		return "redirect:/";
	}

	// 이미지 설정
	@PutMapping("mypage")
	public ResponseEntity<String> profileUpdate(@RequestParam("profile") MultipartFile profile, HttpSession session)
			throws Exception {
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
		model.addAttribute("questions", Question.values());

		return "forgotPw";
	}

	@PostMapping("forgotPw")
	public ResponseEntity<Object> forgotPassword(HttpSession session, @RequestParam("email") String email,
			@RequestParam("question") Question question, @RequestParam("answer") String answer) {
		Seller findSeller = sellerService.findSellerByEmail(email);
		if (findSeller == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		if (findSeller.getQuestion().equals(question) && findSeller.getAnswer().equals(answer)) {
			session.setAttribute("email", email);
			return ResponseEntity.status(HttpStatus.OK).body(findSeller);
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	}

	@GetMapping("forgotPw/setPw")
	public String setPw(Model model, HttpSession session) {
		String email = (String) session.getAttribute("email");
		if (email != null) {
			Seller seller = sellerService.findSellerByEmail(email);
			model.addAttribute("seller", seller);
			return "setPw";
		}
		return "redirect:/";
	}

	@PostMapping("forgotPw/setPw")
	public String setPwd(HttpSession session, @RequestParam("password") String password) {
		String email = (String) session.getAttribute("email");
		if (email != null) {
			Seller seller = sellerService.findSellerByEmail(email);
			seller.setPassword(password);
			sellerService.saveSeller(seller);

			return "redirect:/";
		}
		return "redirect:/";
	}
}
