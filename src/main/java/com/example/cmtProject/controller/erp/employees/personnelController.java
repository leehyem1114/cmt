package com.example.cmtProject.controller.erp.employees;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.cmtProject.repository.erp.employees.EmployeesRepository;
import com.example.cmtProject.service.erp.employees.EmployeesService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/emp")
public class personnelController {
	@Autowired private EmployeesService empService;

	@GetMapping("/")
	public String main() {
		return "erp/personnelMgt/emp/home";
	}
	/***나의 인사카드***/
	@GetMapping("/myEmplist")
	public String list() {
		return "erp/personnelMgt/emp/myEmplist";
	}
	
	/***사원 인사카드 조회 SELECT***/
	@GetMapping("/emplist")
	public String empList(HttpSession session,Model model) {
		
		
		
		return "erp/personnelMgt/emp/emplist";
	}
}
