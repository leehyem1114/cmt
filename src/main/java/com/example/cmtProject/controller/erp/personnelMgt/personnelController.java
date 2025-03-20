package com.example.cmtProject.controller.erp.personnelMgt;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/emp")
public class personnelController {

	@GetMapping("/")
	public String main() {
		return "erp/personnelMgt/emp/emp/home";
	}
	
	
	/****나의 정보 조회****/
	@GetMapping("/myEmpList")
	public String myEmpList() {
		
		return "erp/personnelMgt/emp/myEmpList";
	}
//	@GetMapping("/myEmpList/{empId}")
//	public String myEmpListId() {
//		
//		return "erp/personnelMgt/emp/myEmpList";
//	}
	
	/****전체 사원 조회***ㄴ*/
	@GetMapping("/empList")
	public String empList() {
		
		
		return "erp/personnelMgt/emp/empList";
	}
	
	
}
