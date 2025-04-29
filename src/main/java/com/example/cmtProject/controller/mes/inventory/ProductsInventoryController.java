package com.example.cmtProject.controller.mes.inventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.cmtProject.constants.PathConstants;
import com.example.cmtProject.mapper.mes.inventory.ProductsInventoryMapper;
import com.example.cmtProject.service.mes.inventory.ProductsInventoryService;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/productsinventory")
@Slf4j

public class ProductsInventoryController {
	
	@Autowired
	private ProductsInventoryService pis;
	
	/**
	 * 
	 * 완재품 재고 메인페이지
	 * 
	 */
	@GetMapping("/view")
	public String meterialInventoryGET(Model model) {
		Map<String,Object> findMap = new HashMap<>();
		List<Map<String,Object>> pInventoryList = pis.pInventoryList(findMap);
		model.addAttribute("pInventoryList", pInventoryList);
		
		return "mes/inventory/pInventoryList";
	}
	

}
