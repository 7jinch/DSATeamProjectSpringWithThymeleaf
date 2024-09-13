package com.example.tabitabi.DTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class MessageDTO {

	private Long id;

	private String sender;

	private String content;

	private Long chatRoomId;
	
	public MessageDTO(Long id, String sender, String content, Long chatRoomId) {
		this.id = id;
		this.sender = sender;
		this.content = content;
		this.chatRoomId = chatRoomId;
	}
}