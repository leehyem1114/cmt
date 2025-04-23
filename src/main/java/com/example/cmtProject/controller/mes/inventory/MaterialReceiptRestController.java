package com.example.cmtProject.controller.mes.inventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.cmtProject.comm.response.ApiResponse;
import com.example.cmtProject.service.mes.inventory.MaterialReceiptService;

import lombok.extern.slf4j.Slf4j;

/**
 * 원자재 입고관리 REST 컨트롤러
 * 원자재 입고 조회 및 처리 관련 RESTful API를 제공합니다.
 */
@RestController
@RequestMapping("api/materialreceipt")
@Slf4j
public class MaterialReceiptRestController {
	
	@Autowired
	private MaterialReceiptService mrs;
	
	/**
	 * 입고 목록 조회 API
	 * 
	 * @param keyword 검색 키워드 (선택)
	 * @return 입고 목록 데이터
	 */
	@GetMapping("/list")
	public ApiResponse<List<Map<String, Object>>> getmReceipt(
	        @RequestParam(name = "keyword", required = false) String keyword) {
		
		Map<String, Object> findMap = new HashMap<>();
		if (keyword != null && !keyword.trim().isEmpty()) {
            findMap.put("keyword", keyword);
        }
        
        List<Map<String, Object>> mReceipt = mrs.receiptList(findMap);
		
		return ApiResponse.success(mReceipt);
	}
	
	/**
	 * 발주 정보를 바탕으로 입고 정보 생성 API
	 * 
	 * @return 처리 결과
	 */
	@PostMapping("/register-all")
	public ApiResponse<Map<String, Object>> registerAllFromPurchaseOrders() {
	    Map<String, Object> result = mrs.createReceiptFromPurchaseOrder();
	    return ApiResponse.success(result);
	}
	
	/**
	 * 입고 상세 정보 조회 API
	 * 
	 * @param receiptNo 입고 번호
	 * @return 입고 상세 정보
	 */
	@GetMapping("/detail/{receiptNo}")
	public ApiResponse<Map<String, Object>> getReceiptDetail(@PathVariable("receiptNo") Long receiptNo) {
	    Map<String, Object> detail = mrs.getReceiptDetail(receiptNo);
	    return ApiResponse.success(detail);
	}
	
	/**
	 * 입고 확정 처리 API
	 * 선택한 입고 항목의 상태를 "입고완료"로 변경합니다.
	 * 
	 * @param params 입고 확정 처리할 항목 정보
	 * @return 처리 결과
	 */
	@PostMapping("/confirm")
	public ApiResponse<Map<String, Object>> confirmReceipt(@RequestBody Map<String, Object> params) {
	    Map<String, Object> result = mrs.confirmReceipt(params);
	    return ApiResponse.success(result);
	}
	
	/**
	 * 검수 등록 처리 API
	 * 선택한 입고 항목의 상태를 "검수중"으로 변경합니다.
	 * 
	 * @param params 검수 등록 처리할 항목 정보
	 * @return 처리 결과
	 */
	@PostMapping("/inspection")
	public ApiResponse<Map<String, Object>> registerInspection(@RequestBody Map<String, Object> params) {
	    Map<String, Object> result = mrs.requestInspection(params);
	    return ApiResponse.success(result);
	}

} //MaterialInventoryRestController