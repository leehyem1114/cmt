package com.example.cmtProject.controller.erp.personnelMgt;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/emp")
public class personnelController {

	@GetMapping("/")
	public String main() {
		return "erp/personnelMgt/emp/home";
	}
	/***나의 인사카드***/
	@GetMapping("/myEmplist")
	public String list() {
		return "erp/personnelMgt/emp/myEmplist";
	}
	
	/***사원 인사카드***/
	@GetMapping("/emplist")
	public String empList() {
		return "erp/personnelMgt/emp/emplist";
	}
}
