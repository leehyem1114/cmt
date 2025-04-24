package com.example.cmtProject.service.mes.inventory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.cmtProject.mapper.mes.inventory.MaterialReceiptMapper;
import com.example.cmtProject.service.mes.qualityControl.IqcService;

import lombok.extern.slf4j.Slf4j;

/**
 * 원자재 입고 처리 서비스
 * 원자재 입고 조회 및 처리 관련 비즈니스 로직을 제공합니다.
 */
@Service
@Slf4j
public class MaterialReceiptService {
	
	@Autowired
	private MaterialReceiptMapper mrm;
	
	@Autowired
	private IqcService iqcService;
	
	/**
	 * 입고 목록 조회
	 * 
	 * @param map 검색 조건
	 * @return 입고 목록
	 */
	public List<Map<String, Object>> receiptList(Map<String,Object> map){
		return mrm.mReceiptList(map);
	}
	
	/**
	 * 미입고 상태인 발주 목록 조회
	 * 
	 * @param map 검색 조건
	 * @return 발주 목록
	 */
	public List<Map<String, Object>> puchasesList(Map<String,Object> map){
		return mrm.puchasesList(map);
	}

	/**
	 * 발주 정보 바탕으로 입고정보 생성
	 * 
	 * @return 처리 결과
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
	
	/**
	 * 입고 상세 정보 조회
	 * 
	 * @param receiptNo 입고 번호
	 * @return 입고 상세 정보
	 */
	public Map<String, Object> getReceiptDetail(Long receiptNo) {
	    Map<String, Object> resultMap = new HashMap<>();
	    
	    try {
	        // 기본 입고 정보 조회
	        Map<String, Object> receiptDetail = mrm.getReceiptDetail(receiptNo);
	        
	        if (receiptDetail == null) {
	            resultMap.put("success", false);
	            resultMap.put("message", "해당 입고 정보를 찾을 수 없습니다.");
	            return resultMap;
	        }
	        
	        // 조회 결과 병합
	        resultMap.putAll(receiptDetail);
	        
	        // 검수 정보 조회 (있는 경우)
//	        Map<String, Object> inspectionData = mrm.getInspectionInfo(receiptNo);
//	        resultMap.put("hasInspection", inspectionData != null);
//	        if (inspectionData != null) {
//	            resultMap.put("inspectionData", inspectionData);
//	        }
	        
	        // LOT 정보 조회
//	        List<Map<String, Object>> lotData = mrm.getLotInfo(receiptNo);
//	        resultMap.put("lotData", lotData != null ? lotData : new ArrayList<>());
	        
	        // 위치 정보 조회
//	        List<Map<String, Object>> locationData = mrm.getLocationInfo(receiptNo);
//	        resultMap.put("locationData", locationData != null ? locationData : new ArrayList<>());
	        
	        // 이력 정보 조회
//	        List<Map<String, Object>> historyData = mrm.getHistoryInfo(receiptNo);
//	        resultMap.put("historyData", historyData != null ? historyData : new ArrayList<>());
	        
	        resultMap.put("success", true);
	    } catch (Exception e) {
	        log.error("입고 상세 정보 조회 중 오류 발생: {}", e.getMessage(), e);
	        resultMap.put("success", false);
	        resultMap.put("message", "상세 정보 조회 중 오류가 발생했습니다: " + e.getMessage());
	    }
	    
	    return resultMap;
	}
	
	/**
	 * 입고 확정 처리
	 * 
	 * @param params 입고 확정 처리할 항목 정보
	 * @return 처리 결과
	 */
	@Transactional
	public Map<String, Object> confirmReceipt(Map<String, Object> params) {
	    Map<String, Object> resultMap = new HashMap<>();
	    int updateCount = 0;
	    
	    try {
	        // 확정 처리할 항목 목록
	        @SuppressWarnings("unchecked")
	        List<Map<String, Object>> items = (List<Map<String, Object>>) params.get("items");
	        
	        if (items == null || items.isEmpty()) {
	            resultMap.put("success", false);
	            resultMap.put("message", "확정 처리할 항목이 없습니다.");
	            return resultMap;
	        }
	        
	        LocalDate now = LocalDate.now();
	        String nowStr = now.toString();
	        
	        for (Map<String, Object> item : items) {
	            try {
	                // 입고 상태 업데이트
	                Map<String, Object> updateMap = new HashMap<>();
	                updateMap.put("receiptNo", item.get("receiptNo"));
	                updateMap.put("receiptStatus", item.get("receiptStatus"));
	                updateMap.put("updatedBy", item.get("updatedBy"));
	                updateMap.put("updatedDate", nowStr);
	                
	                int result = mrm.updateReceiptStatus(updateMap);
	                
	                if (result > 0) {
	                    updateCount++;
	                    
	                    // 재고 이력 기록
	                    Map<String, Object> historyMap = new HashMap<>();
	                    historyMap.put("receiptNo", item.get("receiptNo"));
	                    historyMap.put("actionType", "RECEIPT_CONFIRMED");
	                    historyMap.put("actionDescription", "입고 확정 처리됨");
	                    historyMap.put("actionUser", item.get("updatedBy"));
	                    historyMap.put("actionDate", nowStr);
	                    
//	                    mrm.insertReceiptHistory(historyMap);
	                    
	                    // 재고 정보 업데이트 - 실제 입고 수량만큼 재고 증가
	                    Map<String, Object> receiptDetail = mrm.getReceiptDetail((Long)item.get("receiptNo"));
	                    if (receiptDetail != null) {
	                        Map<String, Object> stockMap = new HashMap<>();
	                        stockMap.put("mtlCode", receiptDetail.get("MTL_CODE"));
	                        stockMap.put("warehouseCode", receiptDetail.get("WAREHOUSE_CODE"));
	                        stockMap.put("locationCode", receiptDetail.get("LOCATION_CODE"));
	                        stockMap.put("stockQty", receiptDetail.get("RECEIVED_QTY"));
	                        stockMap.put("updatedBy", item.get("updatedBy"));
	                        
	                        // 재고 업데이트 (있으면 증가, 없으면 생성)
//	                        mrm.updateMaterialInventory(stockMap);
	                    }
	                }
	            } catch (Exception e) {
	                log.error("개별 입고 확정 처리 중 오류 발생: {}", e.getMessage(), e);
	            }
	        }
	        
	        if (updateCount > 0) {
	            resultMap.put("success", true);
	            resultMap.put("message", updateCount + "개 항목의 입고 확정이 완료되었습니다.");
	            resultMap.put("updateCount", updateCount);
	        } else {
	            resultMap.put("success", false);
	            resultMap.put("message", "입고 확정 처리가 실패했습니다.");
	        }
	        
	        return resultMap;
	    } catch (Exception e) {
	        log.error("입고 확정 처리 중 오류 발생: {}", e.getMessage(), e);
	        resultMap.put("success", false);
	        resultMap.put("message", "오류가 발생했습니다: " + e.getMessage());
	        return resultMap;
	    }
	}
	
	/**
	 * 검수 요청 처리 - 입고일을 검수 요청일로 설정
	 * 
	 * @param params 검수 요청 정보
	 * @return 처리 결과
	 */
	@Transactional
	public Map<String, Object> requestInspection(Map<String, Object> params) {
	    Map<String, Object> resultMap = new HashMap<>();
	    
	    try {
	        Long receiptNo = null;
	        if (params.get("receiptNo") instanceof Long) {
	            receiptNo = (Long) params.get("receiptNo");
	        } else if (params.get("receiptNo") instanceof String) {
	            receiptNo = Long.parseLong((String) params.get("receiptNo"));
	        } else if (params.get("receiptNo") instanceof Integer) {
	            receiptNo = ((Integer) params.get("receiptNo")).longValue();
	        }
	        
	        if (receiptNo == null) {
	            resultMap.put("success", false);
	            resultMap.put("message", "입고 번호가 유효하지 않습니다.");
	            return resultMap;
	        }
	        
	        // 현재 날짜를 입고일로 설정
	        LocalDate today = LocalDate.now();
	        String todayStr = today.toString();
	        
	        // 입고 상태 및 입고일 업데이트
	        Map<String, Object> updateMap = new HashMap<>();
	        updateMap.put("receiptNo", receiptNo);
	        updateMap.put("receiptStatus", params.get("receiptStatus"));
	        updateMap.put("receiptDate", todayStr); // 검수 요청일을 입고일로 설정
	        updateMap.put("updatedBy", params.get("updatedBy"));
	        updateMap.put("updatedDate", todayStr);
	        updateMap.put("receiptCode", params.get("receiptCode"));

	        int result = mrm.updateReceiptStatusAndDate(updateMap);
	        iqcService.insertIqcInspection(updateMap);
	        
	        if (result > 0) {
	            // 이력 기록
	            Map<String, Object> historyMap = new HashMap<>();
	            historyMap.put("receiptNo", receiptNo);
	            historyMap.put("actionType", "INSPECTION_REQUESTED");
	            historyMap.put("actionDescription", "검수 등록됨");
	            historyMap.put("actionUser", params.get("updatedBy"));
	            historyMap.put("actionDate", todayStr);
	            
//	            mrm.insertReceiptHistory(historyMap);
	            
	            resultMap.put("success", true);
	            resultMap.put("message", "검수 등록이 완료되었습니다.");
	        } else {
	            resultMap.put("success", false);
	            resultMap.put("message", "검수 등록 처리가 실패했습니다.");
	        }
	        
	    } catch (Exception e) {
	        log.error("검수 등록 처리 중 오류 발생: {}", e.getMessage(), e);
	        resultMap.put("success", false);
	        resultMap.put("message", "오류가 발생했습니다: " + e.getMessage());
	    }
	    
	    return resultMap;
	}
	
} //MaterialReceiptService