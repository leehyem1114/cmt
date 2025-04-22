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
	
    // 단일 발주 입고 처리 API
    @PostMapping("/register")
    public ApiResponse<Map<String, Object>> registerReceipt(@RequestParam("poNo") Long poNo) {
        // 현재 로그인한 사용자 정보 (임시로 "admin"으로 설정)
        String userName = "admin";
        
        Map<String, Object> result = mrs.createReceiptFromPurchaseOrder(poNo, userName);
        return ApiResponse.success(result);
    }
    
 // 전체 발주 일괄 입고 처리 API
    @PostMapping("/register-all")
    public ApiResponse<Map<String, Object>> registerAllReceipts() {
        // 현재 로그인한 사용자 정보 (임시로 "admin"으로 설정)
        String userName = "admin";
        
        // 모든 미입고 발주 조회
        Map<String, Object> findMap = new HashMap<>();
        List<Map<String, Object>> purchaseOrders = mrs.puchasesList(findMap);
        
        // 결과 저장용 맵
        Map<String, Object> resultMap = new HashMap<>();
        int totalCount = purchaseOrders.size();
        int successCount = 0;
        int failCount = 0;
        
        // 각 발주에 대해 입고 처리
        for (Map<String, Object> order : purchaseOrders) {
            Long poNo = Long.valueOf(order.get("PO_NO").toString());
            
            try {
                Map<String, Object> result = mrs.createReceiptFromPurchaseOrder(poNo, userName);
                if ((Boolean)result.get("success")) {
                    successCount++;
                } else {
                    failCount++;
                }
            } catch (Exception e) {
                log.error("발주번호 {} 입고 처리 중 오류: {}", poNo, e.getMessage());
                failCount++;
            }
        }
        
        // 결과 정보 설정
        resultMap.put("totalCount", totalCount);
        resultMap.put("successCount", successCount);
        resultMap.put("failCount", failCount);
        resultMap.put("success", successCount > 0);
        resultMap.put("message", "총 " + totalCount + "건 중 " + successCount + "건 입고 처리 완료");
        
        return ApiResponse.success(resultMap);
    }

}
