package com.example.tabitabi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.tabitabi.model.Product.Product;
import com.example.tabitabi.model.chat.ChatRoom;
import com.example.tabitabi.model.member.Member;
import com.example.tabitabi.model.seller.Seller;
import com.example.tabitabi.repository.ChatRoomRepository;

@Service
public class ChatRoomService {
    @Autowired
    private ChatRoomRepository chatRoomRepository;
    @Autowired
    private ProductService productService;

    public ChatRoom createChatRoom(Long productId, Seller seller, Member member) {
    	Product product = productService.findByProductId(productId);
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setSeller(seller);
        chatRoom.setMember(member);
        chatRoom.setProduct(product);
        return chatRoomRepository.save(chatRoom);
    }
    
    public ChatRoom findChatRoom(Long chatRoomId,Seller seller,Member member) {
    	Product product = productService.findByProductId(chatRoomId);
    	ChatRoom chatRoom = chatRoomRepository.findByProductAndSellerAndMember(product, seller, member);
    	return chatRoom;
    }
    
    public ChatRoom findById(Long chatRoomId) {
    	Optional<ChatRoom> chatRoom = chatRoomRepository.findById(chatRoomId);
    	return chatRoom.orElse(null);
    }
    
    public List<ChatRoom> findBySeller(Seller seller){
    	List<ChatRoom> chatRooms = chatRoomRepository.findBySeller(seller);
    	return chatRooms;
    }
    
    public List<ChatRoom> findByMember(Member member){
    	List<ChatRoom> chatRooms = chatRoomRepository.findByMember(member);
    	return chatRooms;
    }

    public List<ChatRoom> findByProduct(Product product){
    	List<ChatRoom> chatRooms = chatRoomRepository.findByProduct(product);
    	return chatRooms;
    }
}
