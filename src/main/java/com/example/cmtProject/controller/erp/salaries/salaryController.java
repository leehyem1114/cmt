package com.example.cmtProject.controller.erp.salaries;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.cmtProject.dto.erp.SalaryItemDTO;
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
	
	@GetMapping("/erp/salary/salaryItem")
	public ResponseEntity<Map<String, String>> getSalaryItemName(@RequestParam String type) {
		// "수당" → "BONUS", "공제" → "TAX"
	    String dbType = switch (type) {
	        case "수당" -> "BONUS";
	        case "공제" -> "TAX";
	        default -> "";
	    };

	    String name = salaryItemService.getItemNameByType(dbType);

	    Map<String, String> result = new HashMap<>();
	    result.put("name", name);
	    return ResponseEntity.ok(result);
	}
	
	@GetMapping("/salaryList")
	public String salaryListGet() {
		return "erp/salaries/salaryList";
	}
	
	@GetMapping("/payroll")
	public String payrollGet() {
		return "erp/salaries/payroll";
	}
}
