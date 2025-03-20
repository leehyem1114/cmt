package com.example.cmtProject.controller.erp.salaries;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/salary")
public class salaryController {
	
	@GetMapping("/salaryList")
	public String salaryListGet() {
		return "erp/salaries/salaryList";
	}
	
	@GetMapping("/payroll")
	public String payrollGet() {
		return "erp/salaries/payroll";
	}
}
