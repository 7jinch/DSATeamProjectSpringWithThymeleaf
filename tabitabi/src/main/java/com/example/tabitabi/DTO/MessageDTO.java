package com.example.tabitabi.DTO;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class MessageDTO {

   private Long id;

   private String sender;

   private String content;

   private Long chatRoomId;
   
   private LocalDateTime createdTime;
   
   public MessageDTO(Long id, String sender, String content, Long chatRoomId,LocalDateTime createdTime) {
      this.id = id;
      this.sender = sender;
      this.content = content;
      this.chatRoomId = chatRoomId;
      this.createdTime = createdTime;
   }
}