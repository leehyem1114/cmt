package com.example.cmtProject.controller.erp.employees;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.cmtProject.dto.erp.employees.EmpListPreviewDTO;
import com.example.cmtProject.dto.erp.employees.EmpRegistDTO;
import com.example.cmtProject.dto.erp.employees.searchEmpDTO;
import com.example.cmtProject.entity.erp.employees.Employees;
import com.example.cmtProject.entity.erp.employees.PrincipalDetails;
import com.example.cmtProject.service.erp.employees.EmployeesService;

import ch.qos.logback.core.recovery.ResilientSyslogOutputStream;
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
	public String list(@AuthenticationPrincipal PrincipalDetails principalDetails,Model model) {
		//내정보 확인 및 수정기능 넣어야함~~~~~
		Employees loginUser = principalDetails.getUser();
		model.addAttribute("loginUser",loginUser);
		
		
		return "erp/employees/myEmplist";
	}
	
	/***사원 인사카드 조회 SELECT***/
	@GetMapping("/emplist")
	public String empList(HttpSession session,Model model) {
		List<EmpListPreviewDTO> empList = empService.getEmplist();
		model.addAttribute("emplist",empList);
		System.out.println(empList);
		
		//입력값 검증 및 파라미터로 활용할 객체
		searchEmpDTO searchEmpDTO = new searchEmpDTO();
		model.addAttribute("searchEmpDTO",searchEmpDTO);
		
		return "erp/employees/emplist";
	}
	/****사원 셀랙트박스****/
	@PostMapping("/emplist/searchEmp")
	public String searchDept(@ModelAttribute searchEmpDTO searchEmpDTO,Model model) {
		System.out.println("~~~"+searchEmpDTO.getDept());
		List<searchEmpDTO> searchDTO = empService.getSearchDept(searchEmpDTO);
		model.addAttribute("emplist",searchDTO);
		System.out.println(">>>>>>>>"+searchDTO);
		
		return "erp/employees/emplist";
	}
	
	/****사원 등록*****/
	@PostMapping("/empRegi")
	public String empRegist(@ModelAttribute("empRegistDTO") EmpRegistDTO empRegistDTO,Model model) {
		int empRegi = empService.insertEmp(empRegistDTO);
		
		if(empRegi > 0) {
			System.out.println("직원추가 완료~~~~~~~~~~~~~~~~~~!!");
		}
		System.out.println("insert>>"+empRegi);
		return "erp/employees/emplist";
	}
}
