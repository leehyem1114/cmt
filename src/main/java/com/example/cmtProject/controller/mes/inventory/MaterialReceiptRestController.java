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
import com.example.cmtProject.constants.PathConstants;
import com.example.cmtProject.service.mes.inventory.MaterialReceiptService;

import lombok.extern.slf4j.Slf4j;

/**
 * 원자재 입고관리 REST 컨트롤러
 * 원자재 입고 조회 및 처리 관련 RESTful API를 제공합니다.
 */
@RestController
@RequestMapping(PathConstants.API_MATERIALRECEIPT_BASE)
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
    @GetMapping(PathConstants.LIST)
    public ApiResponse<List<Map<String, Object>>> getMaterialReceipt(
            @RequestParam(name = "keyword", required = false) String keyword) {
        
        Map<String, Object> findMap = new HashMap<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            findMap.put("keyword", keyword);
        }
        
        log.info("입고 목록 조회 요청. 검색어: {}", keyword);
        List<Map<String, Object>> mReceipt = mrs.receiptList(findMap);
        log.info("입고 목록 조회 결과: {}건", mReceipt.size());
        
        return ApiResponse.success(mReceipt);
    }
    
    /**
     * 입고 상세 정보 조회 API
     * 
     * @param receiptNo 입고 번호
     * @return 입고 상세 정보
     */
    @GetMapping(PathConstants.DETAIL + "/{receiptNo}")
    public ApiResponse<Map<String, Object>> getReceiptDetail(@PathVariable("receiptNo") Long receiptNo) {
        log.info("입고 상세 정보 조회 요청. 입고번호: {}", receiptNo);
        Map<String, Object> detail = mrs.getReceiptDetail(receiptNo);
        
        if ((Boolean) detail.get("success")) {
            return ApiResponse.success(detail);
        } else {
            return ApiResponse.error(detail.get("message").toString(), detail);
        }
    }
    
    /**
     * 입고 이력 정보 조회 API
     * 
     * @param receiptNo 입고 번호
     * @return 입고 이력 정보 목록
     */
    @GetMapping(PathConstants.HISTORY + "/{receiptNo}")
    public ApiResponse<List<Map<String, Object>>> getReceiptHistory(@PathVariable("receiptNo") Long receiptNo) {
        log.info("입고 이력 정보 조회 요청. 입고번호: {}", receiptNo);
        
        try {
            List<Map<String, Object>> historyList = mrs.getReceiptHistory(receiptNo);
            
            log.info("입고 이력 정보 조회 결과: {}건", historyList.size());
            return ApiResponse.success(historyList);
        } catch (Exception e) {
            log.error("입고 이력 정보 조회 중 오류 발생: {}", e.getMessage(), e);
            return ApiResponse.error("이력 정보 조회 중 오류가 발생했습니다.", null);
        }
    }
    
    /**
     * 발주 정보를 바탕으로 입고 정보 생성 API
     * 
     * @return 처리 결과
     */
    @PostMapping("/register-all")
    public ApiResponse<Map<String, Object>> registerAllFromPurchaseOrders() {
        log.info("발주 정보 기반 입고 등록 요청");
        Map<String, Object> result = mrs.createReceiptFromPurchaseOrder();
        log.info("발주 정보 기반 입고 등록 결과: {}", result);
        
        if ((Boolean) result.get("success")) {
            return ApiResponse.success(result);
        } else {
            return ApiResponse.error(result.get("message").toString(), result);
        }
    }
    
    /**
     * 입고 확정 처리 API
     * 선택한 입고 항목의 상태를 "입고완료"로 변경하고 재고를 반영합니다.
     * 
     * @param params 입고 확정 처리할 항목 정보
     * @return 처리 결과
     */
    @PostMapping(PathConstants.CONFIRM)
    public ApiResponse<Map<String, Object>> confirmReceipt(@RequestBody Map<String, Object> params) {
        log.info("입고 확정 처리 요청: {}", params);
        
        Map<String, Object> result = mrs.confirmReceipt(params);
        
        if ((Boolean) result.get("success")) {
            log.info("입고 확정 처리 성공: {}", result.get("message"));
            return ApiResponse.success(result);
        } else {
            log.warn("입고 확정 처리 실패: {}", result.get("message"));
            return ApiResponse.error(result.get("message").toString(), result);
        }
    }
    
    /**
     * 검수 등록 처리 API - 다건 처리 지원
     * 선택한 입고 항목의 상태를 "검수중"으로 변경합니다.
     * 
     * @param params 검수 등록 처리할 항목 정보
     * @return 처리 결과
     */
    @PostMapping(PathConstants.INSPECTION)
    public ApiResponse<Map<String, Object>> registerInspection(@RequestBody Map<String, Object> params) {
        log.info("검수 등록 처리 요청: {}", params);
        
        Map<String, Object> result = mrs.registerInspectionMultiple(params);
        
        if ((Boolean) result.get("success")) {
            log.info("검수 등록 처리 성공: {}", result.get("message"));
            return ApiResponse.success(result.get("message").toString(), result);
        } else {
            log.warn("검수 등록 처리 실패: {}", result.get("message"));
            return ApiResponse.error(result.get("message").toString(), result);
        }
    }
}