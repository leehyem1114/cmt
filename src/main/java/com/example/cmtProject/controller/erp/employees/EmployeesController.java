package com.example.cmtProject.controller.erp.employees;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.cmtProject.dto.erp.employees.EmpListPreviewDTO;
import com.example.cmtProject.service.erp.employees.EmployeesService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/emp")
public class EmployeesController {
	@Autowired private EmployeesService empService;

	@GetMapping("/")
	public String main() {
		return "erp/employees/emp/home";
	}
	/***나의 인사카드***/
	@GetMapping("/myEmplist")
	public String list() {
		return "erp/employees/myEmplist";
	}
	
	/***사원 인사카드 조회 SELECT***/
	@GetMapping("/emplist")
	public String empList(HttpSession session,Model model) {
		List<EmpListPreviewDTO> empList = empService.getEmplist();
		model.addAttribute("emplist",empList);
		System.out.println(empList);
		
		return "erp/employees/emplist";
	}
}
