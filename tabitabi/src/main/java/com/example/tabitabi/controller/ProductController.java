package com.example.tabitabi.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import com.example.tabitabi.model.Product.Product;
import com.example.tabitabi.model.Product.ProductImage;
import com.example.tabitabi.model.Product.ProductUpdateForm;
import com.example.tabitabi.model.Product.ProductWriteForm;
import com.example.tabitabi.model.member.Member;
import com.example.tabitabi.model.seller.LoginForm;
import com.example.tabitabi.model.seller.Seller;
import com.example.tabitabi.service.ProductService;
import com.example.tabitabi.util.FileService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("products")
@RequiredArgsConstructor
public class ProductController {

	@Value("${file.upload.path}")
	private String uploadPath; // 파일 업로드 경로

	private final ProductService productService;
	private final FileService fileService;

	// 모든 제품의 목록을 조회, product_list 페이지로 이동
	@GetMapping("list")
	public String list(@RequestParam(name = "page", defaultValue = "1") int page,
			@PageableDefault(size = 10, sort = "productId", direction = Direction.ASC) Pageable pageable, Model model,
			@SessionAttribute(name = "loginMember", required = false) Member loginMember // 세션 가져오기
	) {
		Page<Product> productList = productService.getAllProducts(pageable); // 페이지별 제품 목록 조회
		model.addAttribute("productList", productList); // 모델에 제품 목록 추가

		if (loginMember != null)
			model.addAttribute("member", loginMember);

		int totalRecordsCount = (int) productList.getTotalElements(); // 전체 제품 수
		int totalPageCount = productList.getTotalPages(); // 전체 페이지 수

		return "product/product_list"; // 제품 목록 페이지 반환
	}

	// 로그인한 판매자의 제품 목록을 조회, selling_list 페이지로 이동
	@GetMapping("seller/list")
	public String sellerProductList(@RequestParam(name = "page", defaultValue = "1") int page,
			@PageableDefault(size = 10, sort = "productId", direction = Direction.ASC) Pageable pageable,
			@SessionAttribute(name = "loginSeller", required = false) Seller loginSeller, Model model) {

		if (loginSeller == null) { // 로그인된 판매자가 없으면 로그인 페이지로 리다이렉트
			model.addAttribute("loginForm", new LoginForm());
			return "member/loginForm";
		}

		Page<Product> productList = productService.getProductsBySeller(loginSeller, pageable); // 판매자별 제품 목록 조회
		model.addAttribute("productList", productList); // 모델에 제품 목록 추가

		int totalRecordsCount = (int) productList.getTotalElements(); // 전체 제품 수
		int totalPageCount = productList.getTotalPages(); // 전체 페이지 수

		return "seller/selling_list"; // 판매자 제품 목록 페이지 반환
	}

	// 제품 등록 폼 페이지로 이동
	@GetMapping("register")
	public String registerProduct(@SessionAttribute(name = "loginSeller", required = false) Seller loginSeller,
			Model model) {
		if (loginSeller == null) {
			model.addAttribute("loginForm", new LoginForm());
			return "seller/loginForm";
		}
		model.addAttribute("product", new ProductWriteForm()); // 새로운 제품 등록 폼 객체를 모델에 추가
		return "product/product_form"; // 제품 등록 폼 페이지 반환
	}

	// 제품 등록 요청을 처리
	@PostMapping("register")
	public String registerProduct(@Validated @ModelAttribute("product") ProductWriteForm productWriteForm,
			BindingResult result, @SessionAttribute(name = "loginSeller", required = false) Seller loginSeller,
			@RequestParam(name = "file", required = false) MultipartFile file) {
		if (loginSeller == null) { // 로그인된 판매자가 없으면 로그인 페이지로 리다이렉트
			return "redirect:/login";
		}
		if (result.hasErrors()) { // 폼 검증에 실패하면 다시 등록 폼 페이지로 이동
			return "product/product_form";
		}

		Product product = ProductWriteForm.toProduct(productWriteForm); // 폼 데이터를 통해 제품 엔티티 생성
		product.setSeller(loginSeller); // 제품에 판매자 정보 설정

		if (!file.isEmpty()) { // 파일이 있을 경우 파일 저장 후 제품에 저장
			ProductImage productImage = fileService.saveFile(file);
			productImage.setProduct(product);
			productService.createProduct(product, productImage);
		} else {
			productService.createProduct(product, null); // 파일이 없을 경우 제품만 저장
		}

		log.info("상품 저장완료");
		return "redirect:/products/seller/list"; // 제품 목록 페이지로 리다이렉트
	}

	// 소비자가 제품 상세 정보를 조회, read 페이지로 이동
	@GetMapping("read")
	public String read(@RequestParam("id") Long productId,
			@SessionAttribute(name = "loginMember", required = false) Member loginMember, Model model) {
		Product product = productService.findProduct(productId); // 제품 ID로 제품 조회

		if (product == null) { // 제품이 없으면 목록 페이지로 리다이렉트
			return "redirect:/products/list";
		}
		if(loginMember == null) model.addAttribute("member", null);
		else model.addAttribute("member", loginMember);

		model.addAttribute("product", product); // 모델에 제품 추가
		model.addAttribute("uploadPath", uploadPath); // 업로드 경로를 모델에 추가

		ProductImage productImage = productService.findFileByProductId(product); // 제품 ID로 파일 조회
		if (productImage != null) {
			model.addAttribute("productImage", productImage); // 모델에 제품 이미지 추가
		}

		return "product/read"; // 제품 상세 페이지 반환
	}

	// 판매자 자신의 제품의 상세 정보 조회, sellerread 페이지로 이동
	@GetMapping("seller/read")
	public String sellerRead(@RequestParam("id") Long productId,
			@SessionAttribute(name = "loginSeller", required = false) Seller loginSeller, Model model) {
		if (loginSeller == null) { // 로그인된 판매자가 없으면 로그인 페이지로 리다이렉트
			return "redirect:/login";
		}

		Product product = productService.findProduct(productId); // 제품 ID로 제품 조회

		if (product == null) { // 제품이 없으면 목록 페이지로 리다이렉트
			return "redirect:/products/list";
		}

		if (!product.getSeller().getId().equals(loginSeller.getId())) { // 판매자가 제품의 소유자가 아닐 경우 목록 페이지로 리다이렉트
			return "redirect:/products/seller/list";
		}

		model.addAttribute("product", product); // 모델에 제품 추가
		model.addAttribute("uploadPath", uploadPath); // 업로드 경로를 모델에 추가

		ProductImage productImage = productService.findFileByProductId(product); // 제품 ID로 파일 조회
		if (productImage != null) {
			model.addAttribute("productImage", productImage); // 모델에 제품 이미지 추가
		}

		return "seller/sellerread"; // 판매자 제품 상세 페이지 반환
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

	// 제품 수정 폼 페이지로 이동
	@GetMapping("update")
	public String updateForm(@SessionAttribute(name = "loginSeller", required = false) Seller loginSeller,
			@RequestParam(name = "id", required = false) Long productId, Model model) {
		Product findProduct = productService.findProduct(productId); // 제품 ID로 제품 조회

		if (loginSeller == null) {
			model.addAttribute("loginForm", new LoginForm());
			return "seller/loginForm";
		}
		if (findProduct == null || !findProduct.getSeller().getId().equals(loginSeller.getId())) {
			return "redirect:/products/seller/list"; // 제품이 없거나 판매자가 소유자가 아니면 목록 페이지로 리다이렉트
		}

		ProductUpdateForm productUpdateForm = Product.toUpdateForm(findProduct); // 제품을 업데이트 폼 객체로 변환
		model.addAttribute("productUpdateForm", productUpdateForm);

		ProductImage productImage = productService.findFileByProductId(findProduct); // 제품 ID로 파일 조회
		if (productImage != null) {
			model.addAttribute("file", productImage); // 모델에 파일 추가
		}

		return "product/update"; // 제품 수정 페이지 반환
	}

	// 제품 수정 요청을 처리
	@PostMapping("update")
	public String updateProduct(@Validated @ModelAttribute ProductUpdateForm productUpdateForm, BindingResult result,
			@SessionAttribute(name = "loginSeller", required = false) Seller loginSeller,
			@RequestParam(name = "file", required = false) MultipartFile file,
			@RequestParam("productId") Long productId) {
		if (result.hasErrors()) { // 폼 검증에 실패하면 다시 수정 폼 페이지로 이동
			return "product/update";
		}

		Product updateProduct = ProductUpdateForm.toProduct(productUpdateForm); // 폼 데이터를 통해 수정된 제품 엔티티 생성
		productService.updateProduct(updateProduct, productUpdateForm.isFileRemoved(), file); // 제품 업데이트 요청 처리

		return "redirect:/products/seller/list"; // 제품 목록 페이지로 리다이렉트
	}

	// 제품 삭제 요청을 처리
	@PostMapping("delete")
	public String deleteProduct(@RequestParam(name = "id") Long productId,
			@SessionAttribute(name = "loginSeller", required = false) Seller loginSeller) {
		Product product = productService.findProduct(productId); // 제품 ID로 제품 조회

		if (product != null && loginSeller != null && product.getSeller().getId().equals(loginSeller.getId())) {
			productService.deleteProduct(product); // 제품이 존재하고 로그인된 판매자가 소유자일 경우 제품 삭제
		}

		return "redirect:/products/seller/list"; // 제품 목록 페이지로 리다이렉트
	}
}