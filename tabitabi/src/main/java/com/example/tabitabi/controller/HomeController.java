package com.example.tabitabi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class HomeController {
	@GetMapping("/")
	public String home(Model model) {
		log.info("home 실행");

		return "index";
	}
	
	@GetMapping("kudamon/mall")
	public String mall(Model model) {
		log.info("mall 실행");
		
		return "home";
	}
	
}
