package com.itwillbs.shop.member.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.itwillbs.shop.member.dto.MemberDTO;


@Controller
public class MemberController {
	
	@GetMapping("/members/register")
	public String memberRegisterForm(Model model) {
		model.addAttribute("memberDTO", new MemberDTO());
		return "member/member_register_form";
	}
	
	
}














