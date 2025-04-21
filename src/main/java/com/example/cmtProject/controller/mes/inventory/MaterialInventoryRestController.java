package com.example.cmtProject.controller.mes.inventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.cmtProject.comm.response.ApiResponse;
import com.example.cmtProject.service.mes.inventory.MaterialInventoryService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("")
@Slf4j
 
public class MaterialInventoryRestController {
	
	@Autowired
	private MaterialInventoryService mis;
	
	
	public ApiResponse<List<Map<String, Object>>> getmInventory(
			@RequestParam(name = "keyword", required = false) String keyword) {
		
		Map<String, Object> findMap = new HashMap<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            findMap.put("keyword", keyword);
        }
        
        List<Map<String, Object>> mInventory = mis.inventoryList(findMap);
		
		
		return ApiResponse.success(mInventory);
		
	}
	

}
