package com.example.tabitabi.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.HtmlUtils;

import com.example.tabitabi.DTO.MessageDTO;
import com.example.tabitabi.model.chat.Chat;
import com.example.tabitabi.model.chat.ChatRoom;
import com.example.tabitabi.model.chat.Message;
import com.example.tabitabi.model.member.Member;
import com.example.tabitabi.model.seller.Seller;
import com.example.tabitabi.service.ChatRoomService;
import com.example.tabitabi.service.MessageService;
import com.example.tabitabi.service.ProductService;
import com.example.tabitabi.service.SellerService;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class ChatController {

    @Autowired
    private ChatRoomService chatRoomService;
    @Autowired
    private SellerService sellerService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private ProductService productService;
    
    @PostMapping("/createChatRoom")
    public ResponseEntity<Long> createChatRoom(@RequestParam("product") Long productId
    		,@RequestParam("seller") String Selleremail
    		,HttpSession session
    		,Model model) {
    	Seller seller = sellerService.findSeller(Selleremail);
        Member member = (Member) session.getAttribute("loginMember");
        ChatRoom findChatRoom = chatRoomService.findChatRoom(productId, seller, member);
        ChatRoom chatRoom = null;
        if(findChatRoom!=null) {
        	chatRoom = findChatRoom;
        }else {
        	chatRoom = chatRoomService.createChatRoom(productId, seller, member);
        }
        log.info("생성된 chatRoom 아이디: {}", chatRoom.getId());
        return ResponseEntity.ok().body(chatRoom.getId());
    }
	
    @MessageMapping("/sendMessage")
    @SendTo("/topic/messages")
    public Chat sendMessage(Chat chat) {
    	String sender = HtmlUtils.htmlEscape(chat.getSender()); 
        String content = HtmlUtils.htmlEscape(chat.getContent());
        Long chatRoomId = chat.getChatRoomId();
        ChatRoom chatRoom = chatRoomService.findById(chatRoomId);
        messageService.saveMessage(sender, content, chatRoom);

        return new Chat(sender, content,chatRoomId);
    }
    
    @GetMapping("member/chat")
    public String chat(Model model, HttpSession session
    		,@RequestParam("chatRoomId")Long chatRoomId) {
    	log.info("chatRoomId:{}",chatRoomId);
    	Member member = (Member) session.getAttribute("loginMember");
    	ChatRoom chatRoom = chatRoomService.findById(chatRoomId);
    	if(member == null) {
    		return "index";
    	}
    	model.addAttribute("sender",member);
    	model.addAttribute("chatRoom",chatRoom);
    	model.addAttribute("message",messageService.findMessage(chatRoomId));
    	return "chat/chat";
    }
    
    @GetMapping("seller/chat")
    public String sellerChat(Model model, HttpSession session
    		,@RequestParam("chatRoomId") Long chatRoomId) {
    	Seller seller = (Seller) session.getAttribute("loginSeller");
    	ChatRoom chatRoom = chatRoomService.findById(chatRoomId);
    	if(seller != null){
    		model.addAttribute("sender",seller);
    	} else {
    		return "index";
    	}
    	model.addAttribute("chatRoom",chatRoom);
    	return "chat/chat";
    }
    
    @GetMapping("chatList")
    public String chatList(HttpSession session, Model model) {
    	Member member = (Member)session.getAttribute("loginMember");
    	Seller seller = (Seller)session.getAttribute("loginSeller");
    	List<ChatRoom> chatRoom = null;
    	if(member!= null) {
    		model.addAttribute("sender",member);
    		chatRoom = chatRoomService.findByMember(member);
    	}
    	if(seller != null) {
    		model.addAttribute("sender",seller);
    		chatRoom = chatRoomService.findBySeller(seller);
    	}
    	model.addAttribute("chatRoom",chatRoom);

    	return "chat/chatList";
    }
    
    @PostMapping("showChatList")
    public ResponseEntity<List<MessageDTO>> showChatList(@RequestParam("chatRoomId") Long chatRoomId) {
        log.info("chatRoomId: {}", chatRoomId);
        List<Message> messages = messageService.findMessage(chatRoomId);
        List<MessageDTO> messageDTOs = messages.stream()
                .map(message -> new MessageDTO(
                    message.getId(),
                    message.getSender(),
                    message.getContent(),
                    message.getChatRoom().getId()
                ))
                .collect(Collectors.toList());
        log.info("messageDTOs: {}", messageDTOs);
        return ResponseEntity.ok(messageDTOs);
    }
}