package com.example.cmtProject.controller.mes.inventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.cmtProject.service.mes.inventory.MaterialReceiptService;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/materialreceipt")
@Slf4j
public class MaterialReceiptController {
	
	@Autowired
	private MaterialReceiptService mrs;
	
	/**
	 * 
	 * 입고관리 메인페이지
	 * 
	 */
	@GetMapping("view")
	public String meterialReceiptGET(Model model) {
		
		Map<String, Object> findMap = new HashMap<>();
		
		List<Map<String, Object>> mReceipt = mrs.receiptList(findMap);
		
		model.addAttribute("mReceipt", mReceipt);	
		
		return "mes/inventory/materialReceipt";
		
	}
}
