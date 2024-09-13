package com.example.tabitabi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tabitabi.model.chat.ChatRoom;
import com.example.tabitabi.model.chat.Message;


public interface MessageRepository extends JpaRepository<Message, Long> {
	List<Message> findByChatRoom(ChatRoom chatRoom);
}
