package com.example.tabitabi.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.tabitabi.model.chat.ChatRoom;
import com.example.tabitabi.model.chat.Message;
import com.example.tabitabi.repository.MessageRepository;

@Service
public class MessageService {
    
    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private ChatRoomService chatRoomService;
    
    public void saveMessage(String sender,String content,ChatRoom chatRoom,
    		LocalDateTime createdIme) {
    	Message message = new Message();
        message.setSender(sender);
        message.setContent(content);
        message.setChatRoom(chatRoom);
        message.setCreatedtime(createdIme);
    	messageRepository.save(message);
    }
    
    public List<Message> findMessage(Long chatRoomId) {
    	ChatRoom chatRoom = chatRoomService.findById(chatRoomId);
    	return messageRepository.findByChatRoom(chatRoom);
    }
    
}
