package com.example.tabitabi.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.example.tabitabi.DTO.ChatRoomDTO;
import com.example.tabitabi.model.chat.ChatRoom;
import com.example.tabitabi.model.member.Member;
import com.example.tabitabi.model.seller.Seller;
import com.example.tabitabi.service.CartService;
import com.example.tabitabi.service.ChatRoomService;
import com.example.tabitabi.service.MemberService;
import com.example.tabitabi.service.MessageService;
import com.example.tabitabi.service.ProductService;
import com.example.tabitabi.service.WishlistService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {
	private final ProductService productService;
	private final WishlistService wishlistService;
	private final ChatRoomService chatRoomService;
	private final MessageService messageService;
	
	@GetMapping("/")
	public String home(Model model) {
		log.info("index 실행");

		return "index";
	}
	
	@GetMapping("kudamon/mall")
	public String mall(Model model, @SessionAttribute(name = "loginMember", required = false) Member loginMember) {
		log.info("mall()실행");
		List<Object[]> topProducts = productService.getTop10Products();
		List<Object[]> popularzzim = wishlistService.getProductsOrderedByWishCount();
      model.addAttribute("topProducts", topProducts);
      model.addAttribute("popularzzim", popularzzim);
      
      if(loginMember != null) model.addAttribute("memberId", loginMember.getId());
        
      return "mall";
	}
	
	@GetMapping("/joinchoice")
    public String showjoinChoice() {
        return "joinchoice"; 
    }
	
	@GetMapping("/openPopup")
	   public String openPopup(Model model, HttpSession session) {
	       Member member = (Member) session.getAttribute("loginMember");
	       Seller seller = (Seller) session.getAttribute("loginSeller");
	       List<ChatRoom> chatRooms = new ArrayList<>();
	       
	       if (member != null) {
	           model.addAttribute("sender", member);
	           chatRooms = chatRoomService.findByMember(member);
	       }
	       
	       if (seller != null) {
	           model.addAttribute("sender", seller);
	           chatRooms = chatRoomService.findBySeller(seller);
	       }
	       
//	       List<ChatRoomDTO> chatRoomDTOs = chatRooms.stream()
//	           .map(chatRoom -> new ChatRoomDTO(
//	               chatRoom.getId(),
//	               chatRoom.getProduct(),
//	               chatRoom.getMember(),
//	               chatRoom.getSeller(),
//	               messageService.findMessage(chatRoom.getId()).stream()
//	                   .map(message -> new MessageDTO(
//	                       message.getId(),
//	                       message.getSender(),
//	                       message.getContent(),
//	                       message.getChatRoom().getId(),
//	                       message.getCreatedTime()
//	                   ))
//	                   .collect(Collectors.toList()) // List<MessageDTO>로 변환
//	           ))
//	           .collect(Collectors.toList());
//
//	       model.addAttribute("chatRoom", chatRoomDTOs);
	       return "chat/chatList";
	   }


	   @GetMapping("/chatLists")
	   public String chatLists(HttpSession session, @RequestParam("id") long chatRoomId, Model model) {
	      Member member = (Member) session.getAttribute("loginMember");
	      Seller seller = (Seller) session.getAttribute("loginSeller");
	      if (member != null) {
	         return "redirect:/member/chat?chatRoomId=" + chatRoomId;
	      }
	      if (seller != null) {
	         return "redirect:/seller/chat?chatRoomId=" + chatRoomId;
	      }
	      return "/";
	   }

}
