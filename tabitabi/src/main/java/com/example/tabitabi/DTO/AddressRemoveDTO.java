package com.example.tabitabi.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class AddressRemoveDTO {
	private Long memberId;
	private Long addressId;
}
