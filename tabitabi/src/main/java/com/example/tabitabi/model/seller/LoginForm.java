package com.example.tabitabi.model.seller;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class LoginForm {
	@Size(min = 4, max = 20)
	private String email;
	
	@Size(min = 4, max = 20)
	private String password;
}
