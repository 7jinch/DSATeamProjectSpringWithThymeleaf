package com.example.tabitabi.controller;

import java.util.HashMap;
import java.util.List;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;

import com.example.tabitabi.DTO.AddressCreateDTO;
import com.example.tabitabi.DTO.AddressRemoveDTO;
import com.example.tabitabi.DTO.OrderListDTO;
import com.example.tabitabi.model.Question;
import com.example.tabitabi.model.cart.Cart;
import com.example.tabitabi.model.member.LoginForm;
import com.example.tabitabi.model.member.Member;
import com.example.tabitabi.model.member.MemberJoinForm;
import com.example.tabitabi.model.orderAddress.OrderAddress;
import com.example.tabitabi.repository.CartRepository;
import com.example.tabitabi.repository.MemberRepository;
import com.example.tabitabi.service.CartService;
import com.example.tabitabi.service.EmailService;
import com.example.tabitabi.service.MemberService;
import com.example.tabitabi.service.OrderAddressService;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("member")
public class MemberController {

	@Autowired
	private MemberRepository memberRepository;

	private MemberService memberService;

	@Autowired
	private EmailService emailService;

	@Autowired
	public void setMemberService(MemberService memberService) {
		this.memberService = memberService;
	}

	@Autowired
	private CartService cartService;
	@Autowired
	private OrderAddressService addressService;

	private String generateVerificationCode() {
		// randomUUID는 기본적으로 36자 생성, replace로 문자열에서 원하는 부분 지울 수 있다
		// substring 0번째 글자부터 4번째까지만 받아오기
		return java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 5);
	}

	@GetMapping("join")
	public String joinForm(Model model) {
		model.addAttribute("member", new MemberJoinForm());
		model.addAttribute("questions", Question.values());
		return "member/joinForm";
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
	public ResponseEntity<String> join(@Validated @ModelAttribute("member") MemberJoinForm memberJoinForm,
			Model model) {
		Member member = memberJoinForm.toMember(memberJoinForm);
		memberService.saveMember(member);
		cartService.createCart(member); // 장바구니 생성
		log.info("회원가입 컨트롤러: {}", member);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@GetMapping("login")
	public String loginForm(Model model) {
		log.info("로그인 페이지 열기");
		model.addAttribute("loginForm", new LoginForm());
		return "member/loginForm";
	}

	@PostMapping("login")
	public String login(@Validated @ModelAttribute LoginForm loginForm, HttpServletRequest request,
			BindingResult result, HttpServletResponse response, HttpSession session) {
		Member findMember = memberService.findMemberByEmail(loginForm.getEmail());
		if (result.hasErrors()) {
			return "member/loginForm";
		}
		if (findMember == null) {
			result.reject("noID", "아이디가 존재 하지 않습니다.");
			return "member/loginForm";
		}
		if (findMember.getPassword().equals(loginForm.getPassword())) {
			session = request.getSession();
			session.setAttribute("loginMember", findMember);

			return "redirect:/";
		}
		if (request.getSession().getAttribute("loginMember") != null
				|| request.getSession().getAttribute("loginSeller") != null) {
			return "redirect:/";
		}
		result.reject("notPassword", "패스워드가 맞지 않습니다.");
		return "member/loginForm";
	}

	@GetMapping("logout")
	public String logout(HttpServletRequest request) {

		HttpSession session = request.getSession();
		session.setAttribute("loginMember", null);

		return "index";
	}

	@GetMapping("mypage")
	public String mypage(HttpSession session, Model model,
			@SessionAttribute(name = "loginMember", required = false) Member loginMember) {
		if (loginMember == null) {
			model.addAttribute("loginForm", new LoginForm());
			return "member/loginForm";
		}

		Member member = (Member) session.getAttribute("loginMember");
		Member findMember = memberService.findMemberByEmail(member.getEmail());
		model.addAttribute("member", findMember);
		model.addAttribute("questions", Question.values());
		model.addAttribute("defaultQu", findMember.getQuestion());

		return "member/mypage";
	}

	@PostMapping("mypage")
	public ResponseEntity<String> pwcheck(@RequestParam("email") String email,
			@RequestParam("password") String password) {
		if (memberService.findMemberByEmail(email).getPassword().equals(password)) {
			return ResponseEntity.ok("correct password");
		}
		return ResponseEntity.badRequest().body("password is wrong");
	}

	@DeleteMapping("mypage")
	public ResponseEntity<String> removeMember(@RequestParam("email") String email, HttpServletRequest request) {
		HttpSession session = request.getSession();

		memberService.removeMember(email);
		session.invalidate();

		return ResponseEntity.ok("정상삭제");
	}

	@PostMapping("update")
	public String update(@Validated @ModelAttribute Member member) {
		log.info("member:{}", member);
		memberService.updateMember(member);
		return "redirect:/";
	}

	@PutMapping("mypage")
	public ResponseEntity<String> profileUpdate(@RequestParam("profile") MultipartFile profile,
			HttpServletRequest request) throws Exception {
		HttpSession session = request.getSession();
		Member member = (Member) session.getAttribute("loginMember");
		if (profile.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("사진이 전송되지 않았습니다.");
		}
		try {
			String imageUrl = memberService.updateProfile(profile, member);
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
	public ResponseEntity<String> forgotPassword(HttpSession session, @RequestParam("email") String email,
			@RequestParam("question") Question question, @RequestParam("answer") String answer) {
		Member findMember = memberService.findMemberByEmail(email);
		log.info("member:{}", findMember);
		if (findMember == null) {
			// Not_FOUND 404오류 내줌
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		if (findMember.getQuestion().equals(question) && findMember.getAnswer().equals(answer)) {
			session.setAttribute("email", email);
			return ResponseEntity.status(HttpStatus.OK).build();
		}
		// BAD_REQUEST 400오류 내줌
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	}

	@GetMapping("forgotPw/setPw")
	public String setPw(Model model, HttpServletRequest request) {
		// 이메일을 사용하여 Member 객체를 조회
		HttpSession session = request.getSession();
		String email = (String) session.getAttribute("email");
		if (email != null) {
			Member member = memberService.findMemberByEmail(email);
			model.addAttribute("member", member);
			return "setPw";
		}
		return "redirect:/";
	}

	@PostMapping("forgotPw/setPw")
	public String setPwd(HttpSession session, @RequestParam("password") String password) {
		String email = (String) session.getAttribute("email");
		if (email != null) {
			Member member = memberService.findMemberByEmail(email);
			member.setPassword(password);
			memberService.saveMember(member);
			session.setAttribute("email", null);
			return "redirect:/";
		}
		return "redirect:/";
	}

	// 배송지 관리 페이지로 이동
	@GetMapping("address/{memberId}")
	public String addressPage(@PathVariable(name = "memberId") Long memberId, Model model,
			@SessionAttribute(name = "loginMember", required = false) Member loginMember // 세션 가져오기
	) {
		if (loginMember == null) {
			model.addAttribute("loginForm", new LoginForm());
			return "member/loginForm";
		}
		if (!memberId.equals(loginMember.getId()))
			return "member/loginform"; // 로그인 페이지로 이동

		model.addAttribute("memberId", loginMember.getId()); // 회원 담기

		List<OrderAddress> addressList = addressService.findByMember(loginMember); // 배송지 찾기
		model.addAttribute("addressList", addressList); // 배송지 담기
		return "member/addressPage"; // 배송지 관리 페이지로 이종
	}

	// 주소 생성
	@PostMapping("address")
	public ResponseEntity<?> createAddres(@SessionAttribute(name = "loginMember", required = false) Member loginMember,
			@Valid @RequestBody AddressCreateDTO addressDto // 보낸 데이터 담기
	) {
		log.info("받은 데이터: {}", addressDto);

		if (loginMember == null)
			return ResponseEntity.badRequest().body(Map.of("message", "로그인이 필요합니다."));
		Boolean result = addressService.craeteAddress(loginMember, addressDto);
		if (result)
			return ResponseEntity.ok(Map.of("message", "주소 생성"));
		return ResponseEntity.badRequest().body(Map.of("message", "알 수 없는 에러: 관리자에게 문의하세요."));
	}

	// 주소 삭제
	@DeleteMapping("address/{memberId}")
	public ResponseEntity<?> deleteAddress(@SessionAttribute(name = "loginMember", required = false) Member loginMember,
			@Valid @RequestBody AddressRemoveDTO addressDto // 보낸 데이터 담기
	) {
		log.info("받은 데이터: {}", addressDto);

		if (loginMember == null)
			return ResponseEntity.badRequest().body(Map.of("message", "로그인이 필요합니다."));
		Boolean result = addressService.removeAddress(loginMember.getId(), addressDto);
		if (result)
			return ResponseEntity.ok(Map.of("message", "주소 삭제"));
		return ResponseEntity.badRequest().body(Map.of("message", "알 수 없는 에러: 관리자에게 문의하세요."));
	}
}
