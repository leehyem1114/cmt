 package com.example.cmtProject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.cmtProject.entity.erp.employees.Employees;
import com.example.cmtProject.entity.erp.employees.PrincipalDetails;
import com.example.cmtProject.repository.erp.employees.EmployeesRepository;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/")
public class MainController {
	
	@Autowired
	private EmployeesRepository empRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCrypPasswordEncoder;
	
	@GetMapping({"","/"})

	public String main(@AuthenticationPrincipal PrincipalDetails principalDetails, RedirectAttributes redirectAttributes) {
		
		if (principalDetails ==null) {
			return "redirect:/login";
		}

		return "home";
	}
	
	@GetMapping("/login") //로그인폼
	public String loginForm(HttpServletRequest request, Model model) {
		
		return "login";
	}
	
	@GetMapping("/loginSuccess")
	public @ResponseBody String loginSucess() {
		
		System.out.println("login 성공");
		
		//return "redirect:/";
		return "<script>alert('로그인 성공'); location.href='/';</script>";
	}
	
	@GetMapping("/loginFail")
	public @ResponseBody String loginFail() {
		
		System.out.println("login 실패");
		return "<script>alert('[ 로그인 실패 ] \\n아이디와 비밀번호를 다시 확인해주세요'); location.href='/';</script>";
	}
	
	@GetMapping("/joinForm") //회원가입폼
	public String joinForm() {
		return "joinForm";
	}
	
	@PostMapping("/join")
	public String join(@ModelAttribute Employees emp) {
		
		emp.setEmpLevel("ROLE_"+emp.getEmpLevel()); //db에 입력데이터에 ROLE_ 이 붙는다
		System.out.println(emp);
		
		//암호화를 하지 않으면 security로 로그인 할 수 없음
		emp.setEmpPassword(bCrypPasswordEncoder.encode(emp.getEmpPassword()));
		empRepository.save(emp);
        
		return "join";
	}
	
	@GetMapping("/user")
	@ResponseBody
	public String user() {
		
		return "user";
	}
	
	@GetMapping("/manager")
	@ResponseBody
	public String manager() {
		
		return "manager";
	}
	
	@GetMapping("/admin")
	@ResponseBody
	public String admin() {
		
		return "admin";
	}
	
	@Secured("ROLE_ADMIN")
	@GetMapping("/onlyadmin")
	public @ResponseBody String onlyAdmin() {
		
		return "onlyAdmin";
	}
	
	@Secured("ROLE_MANAGER")
	@GetMapping("/onlymanager")
	public @ResponseBody String onlyManager() {
		
		return "onlyManager";
	}
	
	@PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
	@GetMapping("/useradmin")
	@ResponseBody
	public String useradmin() {
		
		return "user or admin";
	}
	
	@PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
	@GetMapping("/manageradmin")
	@ResponseBody
	public String manageradmin() {
		
		return "manager or admin";
	}
}
