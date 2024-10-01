package com.example.tabitabi.DTO;

import java.util.List;

import com.example.tabitabi.model.Product.Product;
import com.example.tabitabi.model.member.Member;
import com.example.tabitabi.model.seller.Seller;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class ChatRoomDTO {
   private Long id;
   private Product product;
   private Member member;
   private Seller seller;
   private List<MessageDTO> messages;
   
   public ChatRoomDTO(Long id, Product product, Member member, Seller seller,List<MessageDTO> messages) {
      this.id = id;
      this.product= product;
      this.member = member;
      this.seller = seller;
      this.messages = messages;
   }
}
