package com.example.tabitabi.model.member;

public enum GenderType {
	MALE("MALE"),FEMALE("FEMALE");

	private String description;

	private GenderType(String descripton){
		this.description= descripton;
	}
	
	public String getDescription() {
		return description;
	}
}
