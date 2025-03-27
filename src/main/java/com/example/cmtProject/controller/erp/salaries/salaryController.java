package com.example.cmtProject.controller.erp.salaries;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.cmtProject.dto.erp.salaries.PayrollDTO;
import com.example.cmtProject.dto.erp.salaries.SalaryItemDTO;
import com.example.cmtProject.entity.Salary;
import com.example.cmtProject.service.comm.CommonService;
import com.example.cmtProject.service.erp.salaries.SalaryItemService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/salary")
public class salaryController {
	@Autowired
	private SalaryItemService salaryItemService;
	@Autowired
	private CommonService commonService;
	
	// 급여 유형 목록
	@GetMapping("/salaryItem")
	public String salaryItemGet(Model model) {
		
		 // 서비스에서 급여 항목 목록 가져오기
	    List<SalaryItemDTO> itemList = salaryItemService.getAllSalaryItems();

	    // 모델에 전달
	    model.addAttribute("salaryItems", itemList);

	    // 등록 폼을 위한 DTO 객체도 미리 전달
	    model.addAttribute("salaryItemDTO", new SalaryItemDTO());
	    
	    System.out.println("아이템 유형 리스트 : " + itemList);
		
		
		return "erp/salaries/salaryItem";
	}
	
	@GetMapping("/getNameByType")
	@ResponseBody
	public Map<String, String> getNameByType(@RequestParam("selectedType") String type) {
		  Map<String, String> result = new HashMap<>();

		   // String name = salaryItemService.getFirstItemNameByType(type);
		  //  result.put("name", name);
		    return result;
	}
	
	// 급여 유형 추가
	@PostMapping("/salaryItems")
	@ResponseBody
	public String salaryItemRegister(@RequestBody SalaryItemDTO salaryItemDTO) {
		System.out.println("확인 : " + salaryItemDTO);
		
		salaryItemService.registerSalaryItem(salaryItemDTO);
	    
	    
	    return "redirect:/salary/salaryItem"; // ✅ 등록 후 리스트 페이지로 리다이렉트
	}
	
	// 급여 유형 수정
//	@PutMapping("/salaryItems/{salItemNo}")
//	@ResponseBody
//	public String salaryItemUpdate(@PathVariable("salItemNo") Long salItemNo, @ModelAttribute("salaryItemDTO") SalaryItemDTO salaryItemDTO) {
//	    salaryItemDTO.setSalItemNo(salItemNo);
//	    
//	    salaryItemService.modifySalaryItem(salaryItemDTO);
//	    
//	    return "redirect:/salary/salaryItem";
//	}
	
	
	
//	@GetMapping("/salaryItem")
//	public String salaryItemGet(@RequestParam("salItemValue") String salItemType) {
//		// "수당" → "BONUS", "공제" → "TAX"
////	    String salType = switch (salItemType) {
////	        case "수당" -> "BONUS";
////	        case "공제" -> "TAX";
////	        default -> "";
////	    };
//
////	    String name = salaryItemService.getItemNameByType(salItemType);
////
////	    Map<String, String> result = new HashMap<>();
////	    result.put("name", name);
//	    return "erp/salaries/salaryItem";
//	}
	
	// 급여 지급 이력
	@GetMapping("/salaryList")
	public String salaryListGet(Model model) {
		List<Salary> salaryList = salaryItemService.getAllSalaries();
		model.addAttribute("salaryList", salaryList);
	    // 등록 폼을 위한 DTO 객체도 미리 전달
	    model.addAttribute("salaryItemDTO", new SalaryItemDTO());
		return "erp/salaries/salaryList";
	}
	
	// 급여 대장
	@GetMapping("/payroll")
	public String payrollGet(Model model) {
		
		return "erp/salaries/payroll";
	}
}
