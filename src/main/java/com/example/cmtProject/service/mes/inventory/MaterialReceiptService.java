package com.example.cmtProject.service.mes.inventory;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.cmtProject.mapper.mes.inventory.MaterialReceiptMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MaterialReceiptService {
	
	@Autowired
	private MaterialReceiptMapper mrm;
	
	/**
	 * 
	 *  입고 상태인 목록 조회
	 */
	
	
	public List<Map<String, Object>> receiptList(Map<String,Object>map){
		
		return mrm.mReceiptList(map);
	}
	
	/**
	 * 
	 *  미입고 상태인 발주 목록 조회
	 */
	public List<Map<String, Object>> puchasesList(Map<String,Object>map){
		
		return mrm.puchasesList(map);
	}

	/**
	 * 발주 정보 바탕으로 입고정보 생성
	 */
	@Transactional
	public Map<String, Object> createReceiptFromPurchaseOrder() {
	    Map<String, Object> resultMap = new HashMap<>();
	    int insertCount = 0;
	    
	    try {
	        // 미입고 상태인 발주 목록 조회
	        Map<String, Object> findMap = new HashMap<>();
	        List<Map<String, Object>> purchaseOrders = mrm.puchasesList(findMap);
	        
	        log.info("조회된 발주 목록 수: {}", purchaseOrders.size());
	        
	        LocalDate now = LocalDate.now();
	        String nowStr = now.toString();
	        
	        for (Map<String, Object> po : purchaseOrders) {
	            try {
	                // 입고 정보 맵 생성
	                Map<String, Object> receiptMap = new HashMap<>();
	                
	                // 필수 파라미터 설정
	                receiptMap.put("receiptCode", "RC" + System.currentTimeMillis() % 10000);
	                receiptMap.put("poCode", po.get("PO_CODE"));
	                receiptMap.put("mtlCode", po.get("MTL_CODE"));
	                receiptMap.put("receivedQty", po.get("PO_QTY"));
	                receiptMap.put("lotNo", "LOT-" + nowStr.replace("-", "") + "-" + insertCount);
//	                receiptMap.put("receiptDate", nowStr);
	                receiptMap.put("receiptStatus", "입고대기");
	                receiptMap.put("warehouseCode", po.get("WHS_CODE"));
	                receiptMap.put("locationCode", "LOC-DEFAULT");
	                receiptMap.put("receiver", "SYSTEM");
	                receiptMap.put("createdBy", "SYSTEM");
	                receiptMap.put("updatedBy", "SYSTEM");
	                receiptMap.put("createdDate", nowStr);
	                receiptMap.put("updatedDate", nowStr);
	                
	                log.info("입고 정보 생성: {}", receiptMap);
	                
	                // 입고 정보 저장
	                int result = mrm.insertMaterialReceipt(receiptMap);
	                log.info("입고 정보 저장 결과: {}", result);
	                
	                if (result > 0) {
	                    insertCount++;
	                }
	            } catch (Exception e) {
	                log.error("개별 입고 정보 처리 중 오류 발생: {}", e.getMessage(), e);
	            }
	        }
	        
	        resultMap.put("success", true);
	        resultMap.put("message", insertCount + "개의 발주 정보를 입고 대기 상태로 등록했습니다.");
	        resultMap.put("insertCount", insertCount);
	    } catch (Exception e) {
	        log.error("전체 입고 처리 중 오류 발생: {}", e.getMessage(), e);
	        resultMap.put("success", false);
	        resultMap.put("message", "오류가 발생했습니다: " + e.getMessage());
	    }
	    
	    return resultMap;
	}
	
	
} //MaterialReceiptService
