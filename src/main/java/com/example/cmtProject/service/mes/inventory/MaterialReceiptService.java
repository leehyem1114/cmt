package com.example.cmtProject.service.mes.inventory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.example.cmtProject.mapper.mes.inventory.MaterialInventoryMapper;
import com.example.cmtProject.mapper.mes.inventory.MaterialReceiptHistoryMapper;
import com.example.cmtProject.mapper.mes.inventory.MaterialReceiptMapper;
import com.example.cmtProject.mapper.mes.inventory.MaterialReceiptStockMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * 원자재 입고 처리 서비스
 * 원자재 입고 조회 및 처리 관련 비즈니스 로직을 제공합니다.
 */
@Service
@Slf4j
public class MaterialReceiptService {
	
	@Autowired
	private MaterialReceiptMapper mRmapper;

	@Autowired
	private MaterialReceiptHistoryMapper mRhmapper;
	
	@Autowired
	private MaterialInventoryMapper mImapper;
	
	@Autowired
	private MaterialReceiptStockMapper mRsmapper;
	
	@Autowired
	private IqcService iqcService;
	
	/**
	 * 입고 목록 조회
	 * 
	 * @param map 검색 조건
	 * @return 입고 목록
	 */
	public List<Map<String, Object>> receiptList(Map<String,Object> map){
		log.info("입고 목록 조회 요청");
		return mRmapper.mReceiptList(map);
	}
	
	/**
	 * 미입고 상태인 발주 목록 조회
	 * 
	 * @param map 검색 조건
	 * @return 발주 목록
	 */
	public List<Map<String, Object>> puchasesList(Map<String,Object> map){
		log.info("미입고 발주 목록 조회 요청");
		return mRmapper.puchasesList(map);
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
	        log.info("발주 정보 기반 입고 등록 시작");
	        
	        // 미입고 상태인 발주 목록 조회
	        Map<String, Object> findMap = new HashMap<>();
	        List<Map<String, Object>> purchaseOrders = mRmapper.puchasesList(findMap);
	        
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
	                int result = mRmapper.insertMaterialReceipt(receiptMap);
	                
	                if (result > 0) {
	                    // 입고 번호 조회
	                    Long receiptNo = mRmapper.getLastReceiptNo();
	                    
	                    // 입고 이력 저장
	                    Map<String, Object> historyMap = new HashMap<>();
	                    historyMap.put("receiptNo", receiptNo);
	                    historyMap.put("actionType", "입고등록");
	                    historyMap.put("actionDescription", "발주번호 " + po.get("PO_CODE") + "의 자동 입고 등록");
	                    historyMap.put("actionUser", "SYSTEM");
	                    historyMap.put("createdBy", "SYSTEM");
	                    
	                    mRhmapper.insertHistory(historyMap);
	                    
	                    insertCount++;
	                    log.info("입고 정보 저장 성공: 입고번호={}", receiptNo);
	                } else {
	                    log.warn("입고 정보 저장 실패: {}", receiptMap);
	                }
	            } catch (Exception e) {
	                log.error("개별 입고 정보 처리 중 오류 발생: {}", e.getMessage(), e);
	            }
	        }
	        
	        resultMap.put("success", true);
	        resultMap.put("message", insertCount + "개의 발주 정보를 입고 대기 상태로 등록했습니다.");
	        resultMap.put("insertCount", insertCount);
	        
	        log.info("발주 정보 기반 입고 등록 완료: {}건", insertCount);
	        
	    } catch (Exception e) {
	        log.error("전체 입고 처리 중 오류 발생: {}", e.getMessage(), e);
	        resultMap.put("success", false);
	        resultMap.put("message", "오류가 발생했습니다: " + e.getMessage());
	        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
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
	    log.info("입고 상세 정보 조회 요청: 입고번호={}", receiptNo);
	    
	    Map<String, Object> resultMap = new HashMap<>();
	    
	    try {
	        // 기본 입고 정보 조회
	        Map<String, Object> receiptDetail = mRmapper.getReceiptDetail(receiptNo);
	        
	        if (receiptDetail == null) {
	            resultMap.put("success", false);
	            resultMap.put("message", "해당 입고 정보를 찾을 수 없습니다.");
	            return resultMap;
	        }
	        
	        // 조회 결과 병합
	        resultMap.putAll(receiptDetail);
	        
	        // 이력 정보 조회
	        List<Map<String, Object>> historyList = mRhmapper.getHistoryByReceiptNo(receiptNo);
	        resultMap.put("historyList", historyList != null ? historyList : new ArrayList<>());
	        
	        // 검수 정보 조회 (있는 경우)

//	        Map<String, Object> inspectionData = mRmapper.getInspectionInfo(receiptNo);
//	        resultMap.put("hasInspection", inspectionData != null);
//	        if (inspectionData != null) {
//	            resultMap.put("inspectionData", inspectionData);
//	        }
	        
	        // LOT 정보 조회
//	        List<Map<String, Object>> lotData = mRmapper.getLotInfo(receiptNo);
//	        resultMap.put("lotData", lotData != null ? lotData : new ArrayList<>());
	        
	        // 위치 정보 조회
//	        List<Map<String, Object>> locationData = mRmapper.getLocationInfo(receiptNo);
//	        resultMap.put("locationData", locationData != null ? locationData : new ArrayList<>());
	        
	        resultMap.put("success", true);
	        
	        log.info("입고 상세 정보 조회 성공: 입고코드={}, 상태={}",
	            receiptDetail.get("RECEIPT_CODE"), receiptDetail.get("RECEIPT_STATUS"));
	        
	    } catch (Exception e) {
	        log.error("입고 상세 정보 조회 중 오류 발생: {}", e.getMessage(), e);
	        resultMap.put("success", false);
	        resultMap.put("message", "상세 정보 조회 중 오류가 발생했습니다: " + e.getMessage());
	    }
	    
	    return resultMap;
	}
	
	/**
	 * 입고 이력 정보 조회
	 * 
	 * @param receiptNo 입고 번호
	 * @return 이력 정보 목록
	 */
	public List<Map<String, Object>> getReceiptHistory(Long receiptNo) {
	    log.info("입고 이력 정보 조회 서비스 호출. 입고번호: {}", receiptNo);
	    
	    List<Map<String, Object>> historyList = mRhmapper.getHistoryByReceiptNo(receiptNo);
	    
	    if (historyList == null) {
	        return new ArrayList<>();
	    }
	    
	    return historyList;
	}
	
	/**
	 * 검수 정보 조회
	 * 
	 * @param receiptNo 입고 번호
	 * @return 검수 정보
	 */
//	public Map<String, Object> getInspectionInfo(Long receiptNo) {
//	    log.info("검수 정보 조회 서비스 호출. 입고번호: {}", receiptNo);
//	    
//	    return mRmapper.getInspectionInfo(receiptNo);
//	}
	
	/**
	 * 입고 확정 처리
	 * 입고 확정 후 재고에 반영하고 FIFO 관리를 위한 입고별 재고 정보를 생성합니다.
	 * 
	 * @param params 입고 확정 처리할 항목 정보
	 * @return 처리 결과
	 */
	@Transactional
	public Map<String, Object> confirmReceipt(Map<String, Object> params) {
	    Map<String, Object> resultMap = new HashMap<>();
	    int updateCount = 0;
	    
	    try {
	        log.info("입고 확정 처리 시작: {}", params);
	        
	        // 확정 처리할 항목 목록
	        @SuppressWarnings("unchecked")
	        List<Map<String, Object>> items = (List<Map<String, Object>>) params.get("items");
	        
	        if (items == null || items.isEmpty()) {
	            resultMap.put("success", false);
	            resultMap.put("message", "확정 처리할 항목이 없습니다.");
	            return resultMap;
	        }
	        
	        List<String> failedItems = new ArrayList<>();
	        
	        LocalDate now = LocalDate.now();
	        String nowStr = now.toString();
	        
	        for (Map<String, Object> item : items) {
	            try {
	                // 입고 번호 추출
	                Long receiptNo = null;
	                if (item.get("receiptNo") instanceof Long) {
	                    receiptNo = (Long) item.get("receiptNo");
	                } else if (item.get("receiptNo") instanceof String) {
	                    receiptNo = Long.parseLong((String) item.get("receiptNo"));
	                } else if (item.get("receiptNo") instanceof Integer) {
	                    receiptNo = ((Integer) item.get("receiptNo")).longValue();
	                }
	                
	                if (receiptNo == null) {
	                    log.warn("입고 번호가 유효하지 않음: {}", item.get("receiptNo"));
	                    failedItems.add(String.valueOf(item.get("receiptCode")));
	                    continue;
	                }
	                
	                // 검수 정보 확인
//	                Map<String, Object> inspectionInfo = getInspectionInfo(receiptNo);
//	                if (inspectionInfo == null) {
//	                    log.warn("검수 정보가 없는 항목: 입고번호={}", receiptNo);
//	                    failedItems.add(String.valueOf(item.get("receiptCode")));
//	                    continue;
//	                }
//	                
	                // 검수 결과 확인 - 합격 또는 조건부 합격만 처리
//	                String inspResult = (String) inspectionInfo.get("INSP_RESULT");
//	                if (!"PASS".equals(inspResult) && !"CONDITIONAL_PASS".equals(inspResult)) {
//	                    log.warn("검수 불합격 항목: 입고번호={}, 결과={}", receiptNo, inspResult);
//	                    failedItems.add(String.valueOf(item.get("receiptCode")));
//	                    continue;
//	                }
	                
	                // 1. 입고 상태 업데이트
	                Map<String, Object> updateMap = new HashMap<>();
	                updateMap.put("receiptNo", receiptNo);
	                updateMap.put("receiptStatus", "입고완료");
	                updateMap.put("updatedBy", item.get("updatedBy"));
	                updateMap.put("updatedDate", nowStr);
	                
	                int result = mRmapper.updateReceiptStatus(updateMap);
	                
	                if (result > 0) {
	                    updateCount++;
	                    
	                    // 2. 재고 이력 기록
	                    Map<String, Object> historyMap = new HashMap<>();
	                    historyMap.put("receiptNo", receiptNo);
	                    historyMap.put("actionType", "입고확정");
	                    historyMap.put("actionDescription", "입고 확정 처리됨");
	                    historyMap.put("actionUser", item.get("updatedBy"));
	                    historyMap.put("createdBy", item.get("updatedBy"));
	                    
	                    mRhmapper.insertHistory(historyMap);
	                    
	                    // 3. 입고 정보 조회
	                    Map<String, Object> receiptDetail = mRmapper.getReceiptDetail(receiptNo);
	                    
	                    if (receiptDetail != null) {
	                        // 4. 재고 정보 업데이트 - 실제 입고 수량만큼 재고 증가
	                        Map<String, Object> inventoryParams = new HashMap<>();
	                        inventoryParams.put("mtlCode", receiptDetail.get("MTL_CODE"));
	                        inventoryParams.put("warehouseCode", receiptDetail.get("WAREHOUSE_CODE"));
	                        inventoryParams.put("locationCode", receiptDetail.get("LOCATION_CODE"));
	                        inventoryParams.put("receivedQty", receiptDetail.get("RECEIVED_QTY"));
	                        inventoryParams.put("updatedBy", item.get("updatedBy"));
	                        
	                        // 재고 업데이트 (있으면 증가, 없으면 생성)
	                        mImapper.mergeInventory(inventoryParams);
	                        
	                        // 5. 입고별 재고 정보 추가 (FIFO 관리용)
	                        Map<String, Object> stockParams = new HashMap<>();
	                        stockParams.put("receiptNo", receiptNo);
	                        stockParams.put("mtlCode", receiptDetail.get("MTL_CODE"));
	                        stockParams.put("remainingQty", receiptDetail.get("RECEIVED_QTY"));
	                        stockParams.put("receiptDate", receiptDetail.get("RECEIPT_DATE"));
	                        stockParams.put("createdBy", item.get("updatedBy"));
	                        
	                        mRsmapper.insertStock(stockParams);
	                        
	                        log.info("입고 확정 처리 완료: 입고번호={}, 자재코드={}, 수량={}",
	                            receiptNo, receiptDetail.get("MTL_CODE"), receiptDetail.get("RECEIVED_QTY"));
	                    }
	                } else {
	                    failedItems.add(String.valueOf(item.get("receiptCode")));
	                }
	            } catch (Exception e) {
	                log.error("개별 입고 확정 처리 중 오류 발생: {}", e.getMessage(), e);
	                failedItems.add(String.valueOf(item.get("receiptCode")));
	            }
	        }
	        
	        if (updateCount > 0) {
	            String message = updateCount + "개 항목의 입고 확정이 완료되었습니다.";
	            if (!failedItems.isEmpty()) {
	                message += String.format(" (%d개 항목 실패)", failedItems.size());
	            }
	            
	            resultMap.put("success", true);
	            resultMap.put("message", message);
	            resultMap.put("updateCount", updateCount);
	            resultMap.put("failedItems", failedItems);
	        } else {
	            resultMap.put("success", false);
	            resultMap.put("message", "입고 확정 처리가 실패했습니다.");
	            resultMap.put("failedItems", failedItems);
	        }
	        
	    } catch (Exception e) {
	        log.error("입고 확정 처리 중 오류 발생: {}", e.getMessage(), e);
	        resultMap.put("success", false);
	        resultMap.put("message", "오류가 발생했습니다: " + e.getMessage());
	        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
	    }
	    
	    return resultMap;
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
            log.info("검수 등록 처리 시작: {}", params);
            
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
            updateMap.put("receiptStatus", "검수중");
            updateMap.put("receiptDate", todayStr); // 검수 요청일을 입고일로 설정
            updateMap.put("updatedBy", params.get("updatedBy"));
            updateMap.put("updatedDate", todayStr);
            
            int result = mRmapper.updateReceiptStatusAndDate(updateMap);
            
            if (result > 0) {
                // 이력 기록
                Map<String, Object> historyMap = new HashMap<>();
                historyMap.put("receiptNo", receiptNo);
                historyMap.put("actionType", "검수시작");
                historyMap.put("actionDescription", "검수 등록됨");
                historyMap.put("actionUser", params.get("updatedBy"));
                historyMap.put("createdBy", params.get("updatedBy"));
                
                mRhmapper.insertHistory(historyMap);
                
                log.info("검수 등록 처리 완료: 입고번호={}", receiptNo);
                
                resultMap.put("success", true);
                resultMap.put("message", "검수 등록이 완료되었습니다.");
            } else {
                log.warn("검수 등록 처리 실패: 입고번호={}", receiptNo);
                resultMap.put("success", false);
                resultMap.put("message", "검수 등록 처리가 실패했습니다.");
            }
        } catch (Exception e) {
            log.error("검수 등록 처리 중 오류 발생: {}", e.getMessage(), e);
            resultMap.put("success", false);
            resultMap.put("message", "오류가 발생했습니다: " + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        
        return resultMap;
    }
    
    /**
     * 다건 검수 등록 처리 
     * 다수의 입고 항목에 대해 검수 등록을 처리합니다.
     * 
     * @param params 검수 요청 정보 (items: 검수 등록할 항목 목록)
     * @return 처리 결과
     */
    @Transactional
    public Map<String, Object> registerInspectionMultiple(Map<String, Object> params) {
        Map<String, Object> resultMap = new HashMap<>();
        int successCount = 0;
        List<String> failedItems = new ArrayList<>();
        
        try {
            log.info("다건 검수 등록 처리 시작: {}", params);
            
            // 다건 처리 지원
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> items = (List<Map<String, Object>>) params.get("items");
            
            if (items == null || items.isEmpty()) {
                resultMap.put("success", false);
                resultMap.put("message", "검수 등록 처리할 항목이 없습니다.");
                return resultMap;
            }
            
            // 각 항목에 대해 처리
            for (Map<String, Object> item : items) {
                try {
                    // 개별 항목 처리
                    Map<String, Object> itemResult = requestInspection(item);
                    
                    if ((Boolean) itemResult.get("success")) {
                        successCount++;
                    } else {
                        failedItems.add(String.valueOf(item.get("receiptCode")));
                    }
                } catch (Exception e) {
                    log.error("개별 검수 등록 처리 중 오류: {}", e.getMessage(), e);
                    failedItems.add(String.valueOf(item.get("receiptCode")));
                }
            }
            
            // 결과 집계
            resultMap.put("totalCount", items.size());
            resultMap.put("successCount", successCount);
            resultMap.put("failedCount", failedItems.size());
            resultMap.put("failedItems", failedItems);
            
            if (successCount > 0) {
                String message = String.format("%d개 항목의 검수 등록이 완료되었습니다.", successCount);
                if (!failedItems.isEmpty()) {
                    message += String.format(" (%d개 항목 실패)", failedItems.size());
                }
                
                resultMap.put("success", true);
                resultMap.put("message", message);
                
                log.info("다건 검수 등록 처리 완료: {}", message);
            } else {
                resultMap.put("success", false);
                resultMap.put("message", "검수 등록 처리에 실패했습니다.");
                
                log.warn("다건 검수 등록 처리 실패: 모든 항목 처리 실패");
            }
            
        } catch (Exception e) {
            log.error("다건 검수 등록 처리 중 오류 발생: {}", e.getMessage(), e);
            resultMap.put("success", false);
            resultMap.put("message", "오류가 발생했습니다: " + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        
        return resultMap;
    }
    
    /**
     * 검수 완료 여부 확인
     * 검수가 완료되었는지 확인합니다. (합격, 조건부 합격만 OK)
     * 
     * @param receiptNo 입고 번호
     * @return 검수 완료 여부
     */
//    public boolean isInspectionCompleted(Long receiptNo) {
//        try {
//            // 검수 정보 조회
//            Map<String, Object> inspectionInfo = getInspectionInfo(receiptNo);
//            
//            if (inspectionInfo == null) {
//                return false;
//            }
//            
//            // 검수 결과 확인
//            String inspResult = (String) inspectionInfo.get("INSP_RESULT");
//            
//            // 합격 또는 조건부 합격만 처리 가능
//            return "PASS".equals(inspResult) || "CONDITIONAL_PASS".equals(inspResult);
//            
//        } catch (Exception e) {
//            log.error("검수 완료 여부 확인 중 오류: {}", e.getMessage(), e);
//            return false;
//        }
//    }
}