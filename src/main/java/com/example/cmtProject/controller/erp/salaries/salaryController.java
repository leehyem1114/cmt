package com.example.cmtProject.controller.erp.salaries;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.cmtProject.dto.erp.SalaryItemDTO;
import com.example.cmtProject.entity.Salary;
import com.example.cmtProject.entity.SalaryItemType;
import com.example.cmtProject.service.erp.salaries.SalaryItemService;

@Controller
@RequestMapping("/salary")
public class salaryController {
	@Autowired
	private SalaryItemService salaryItemService;
	
//	@GetMapping("/salaryItem")
//	public String salaryItemGet() {
//		return "erp/salaries/salaryItem";
//	}
	@GetMapping("/salaryItem")
	public String salaryItemGet(Model model) {
		
		 // 서비스에서 급여 항목 목록 가져오기
	    List<SalaryItemDTO> salaryItems = salaryItemService.getAllSalaryItems();

	    // 모델에 전달
	    model.addAttribute("salaryItems", salaryItems);

	    // 등록 폼을 위한 DTO 객체도 미리 전달
	    model.addAttribute("salaryItemDTO", new SalaryItemDTO());
	    
	    System.out.println("아이템 유형 리스트 : " + salaryItems);
		
		
		return "erp/salaries/salaryItem";
	}
	
	@GetMapping("/getNameByType")
	@ResponseBody
	public Map<String, String> getNameByType(@RequestParam("selectedType") SalaryItemType type) {
		  Map<String, String> result = new HashMap<>();

		    String name = salaryItemService.getFirstItemNameByType(type);
		    result.put("name", name);
		    return result;
	}
	
	
	@PostMapping("/salaryItems")
	public String saveSalaryItem(@ModelAttribute SalaryItemDTO salaryItemDTO) {
		System.out.println("확인 : " + salaryItemDTO);
		
		salaryItemService.saveSalaryItem(salaryItemDTO);
	    
	    
	    return "redirect:/salary/salaryItem"; // ✅ 등록 후 리스트 페이지로 리다이렉트
	}
	
	
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
	
	@GetMapping("/salaryList")
	public String salaryListGet(Model model) {
		List<Salary> salaryList = salaryItemService.getAllSalaries();
		model.addAttribute("salaries", salaryList);
		return "erp/salaries/salaryList";
	}
	
	@GetMapping("/payroll")
	public String payrollGet() {
		return "erp/salaries/payroll";
	}
}
