package com.example.tabitabi.model.chat;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Chat {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long chatRoomId;
	
	private String sender;
	
    private String content;
    
    private LocalDateTime createdtime;

    public Chat() {
    }
    
    public Chat(String sender, String content,Long chatRoomId) {
        this.sender = sender;
        this.content = content;
        this.chatRoomId = chatRoomId;
    }
}
