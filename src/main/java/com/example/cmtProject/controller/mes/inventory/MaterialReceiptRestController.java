package com.example.cmtProject.controller.mes.inventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.cmtProject.comm.response.ApiResponse;
import com.example.cmtProject.service.mes.inventory.MaterialReceiptService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("api/materialreceipt")
@Slf4j
public class MaterialReceiptRestController {
	
	@Autowired
	private MaterialReceiptService mrs;
	
	@GetMapping("/list")
	public ApiResponse<List<Map<String, Object>>> getmReceipt() {
		
		Map<String, Object> findMap = new HashMap<>();
        
        List<Map<String, Object>> mReceipt = mrs.receiptList(findMap);
		
		return ApiResponse.success(mReceipt);
	}
	
	@PostMapping("/register-all")
	public ApiResponse<Map<String, Object>> registerAllFromPurchaseOrders() {
	    Map<String, Object> result = mrs.createReceiptFromPurchaseOrder();
	    return ApiResponse.success(result);
	}
	

} //MaterialInventoryRestController
