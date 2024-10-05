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
import com.example.tabitabi.DTO.MyPageMyDataDTO;
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
	public ResponseEntity<?> join(@Validated @ModelAttribute("member") MemberJoinForm memberJoinForm,
			Model model) {
		log.info("join 실행");
		
		Member member = memberJoinForm.toMember(memberJoinForm);
		String memberEmail = member.getEmail();
		if(memberService.findMemberByEmail(memberEmail) != null) {
//	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
			return ResponseEntity.badRequest().body(Map.of("message", "이미 가입한 이메일이 존재합니다."));
		}
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
	    log.info("login 실행");
	    
	    // 로그인 시도 전에 결과 체크
	    if (result.hasErrors()) {
	        return "loginpopup"; // 에러 발생 시 로그인 페이지로 돌아감
	    }

	    // 이메일로 회원 조회
	    Member findMember = memberService.findMemberByEmail(loginForm.getEmail());
	    if (findMember == null) {
	        result.reject("noID", "아이디가 존재 하지 않습니다.");
	        return "loginpopup"; // 이메일이 없으면 로그인 페이지로 돌아감
	    }
	    
	    // 비밀번호 체크 (비밀번호 해시 검증 필요 시 적절한 해시 알고리즘 사용)
	    if (!findMember.getPassword().equals(loginForm.getPassword())) {
	        result.reject("notPassword", "패스워드가 맞지 않습니다.");
	        return "loginpopup"; // 비밀번호가 다르면 로그인 페이지로 돌아감
	    }

	    // 로그인 성공 시 세션에 정보 저장
	    session.setAttribute("loginMember", findMember);

	    return "redirect:/"; // 로그인 성공 시 메인 페이지로 리다이렉트
	}

	@GetMapping("logout")
	public String logout(HttpServletRequest request) {
		log.info("logout 실행");
		HttpSession session = request.getSession();
		session.setAttribute("loginMember", null);
		session.invalidate();

		return "redirect:/kudamon/mall";
	}

	@GetMapping("mypage")
	public String mypage(ServletRequest request, Model model) {
		log.info("mypage 실행");
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpSession session = httpRequest.getSession(false);
		Member member = (Member) session.getAttribute("loginMember");
		Member findMember = memberService.findMemberByEmail(member.getEmail());
		model.addAttribute("member",findMember);
		model.addAttribute("questions", Question.values());
		model.addAttribute("defaultQu", findMember.getQuestion());

		return "member/mypage";
	}
	
	// 내 정보 조회
//	@GetMapping("myData")
//	public ResponseEntity<?> myData(@SessionAttribute(name = "loginMember", required = false) Member loginMember){
//		if(loginMember == null) return ResponseEntity.badRequest().body(Map.of("message", "권한이 없습니다."));
//		
//		MyPageMyDataDTO mpmd = new MyPageMyDataDTO();
//		mpmd.setMember(loginMember);
//		mpmd.setQl(Question.values());
//		return ResponseEntity.ok(mpmd);
//	}

	@PostMapping("mypage")
	public ResponseEntity<String> pwcheck(@RequestParam("email") String email,
			@RequestParam("password") String password) {
		log.info("pwcheck 실행");
		if (memberService.findMemberByEmail(email).getPassword().equals(password)) {
			return ResponseEntity.ok("correct password");
		}
		return ResponseEntity.badRequest().body("password is wrong");
	}

	@DeleteMapping("mypage")
	public ResponseEntity<String> removeMember(@RequestParam("email") String email, HttpServletRequest request) {
		log.info("removeMember 실행");
		HttpSession session = request.getSession();

		memberService.removeMember(email);
		session.invalidate();

		return ResponseEntity.ok("정상삭제");
	}

	@PostMapping("update")
	public String update(@Validated @ModelAttribute Member member) {
		log.info("update 실행");
		log.info("member:{}", member);
		memberService.updateMember(member);
		return "redirect:/member/mypage";
	}

	@PutMapping("mypage")
	public ResponseEntity<String> profileUpdate(@RequestParam("profile") MultipartFile profile,
			HttpServletRequest request) throws Exception {
		log.info("profileUpdate 실행");
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
		log.info("forgot 실행");
		model.addAttribute("questions", Question.values());

		return "forgotPw";
	}

	// @RequestParam -> @RequestBody로 수정하기
	@PostMapping("forgotPw")
	public ResponseEntity<String> forgotPassword(HttpSession session, @RequestParam("email") String email,
			@RequestParam("question") Question question, @RequestParam("answer") String answer) {
		log.info("forgotPassword 실행");
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
		log.info("setPw 실행");
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
		log.info("setPwd 실행");
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
	
    @PostMapping("join/emailCheck")
    public ResponseEntity<?> emailCheck(@RequestParam("email") String email){
    	Member member = memberService.findMemberByEmail(email);
    	if(member != null) {
    		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    	}
    	return ResponseEntity.status(HttpStatus.OK).build();
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
