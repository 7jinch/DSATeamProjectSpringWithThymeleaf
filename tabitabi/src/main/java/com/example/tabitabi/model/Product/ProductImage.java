package com.example.tabitabi.model.Product;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
@NoArgsConstructor
@Entity
public class ProductImage {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long productimageid; //첨부파일 아이디
   
   @ManyToOne
   @JoinColumn(name="product_id")
   private Product product;
   
   private String original_filename;   //원본 파일 이름
   private String saved_filename;      //저장할 파일 이름
   private Long file_size;            //파일 용량
   
   public ProductImage(String original_filename, String saved_filename, Long file_size) {
      this.original_filename = original_filename;
      this.saved_filename = saved_filename;
      this.file_size = file_size;
   }
   
}
