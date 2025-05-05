package com.example.cmtProject.service.mes.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.example.cmtProject.mapper.mes.inventory.ProductsInventoryMapper;
import com.example.cmtProject.mapper.mes.inventory.ProductsIssueStockMapper;
import com.example.cmtProject.mapper.mes.inventory.ProductsMasterMapper;
import com.example.cmtProject.util.SecurityUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProductsInventoryService {
	
	@Autowired
	private ProductsInventoryMapper pImapper;
	
	@Autowired
	private ProductsIssueStockMapper pIsmapper;
	
	@Autowired
	private ProductsMasterMapper productsMasterMapper;
	
	/**
	 * 재고 목록 조회
	 * @param map 검색 조건
	 * @return 재고 목록
	 */
	public List<Map<String,Object>> pInventoryList(Map<String, Object>map){
		return pImapper.pInventoryList(map);
	}
	
	/**
	 * 재고 정보 저장
	 * @param inventoryList 저장할 재고 목록
	 * @return 처리 결과
	 */
	@Transactional
	public Map<String, Object> saveInventory(List<Map<String, Object>> inventoryList) {
		Map<String, Object> resultMap = new HashMap<>();
		int updateCount = 0;
		int insertCount = 0;
		
		try {
			log.info("재고 정보 저장 시작: {}건", inventoryList.size());
			
			// 현재 사용자 ID 가져오기
			String userId = SecurityUtil.getUserId();
			
			for (Map<String, Object> item : inventoryList) {
                // 계산된 가용수량 설정 (가용수량 = 현재수량 - 할당수량)
                double currentQty = Double.parseDouble(item.get("CURRENT_QTY").toString());
                double allocatedQty = Double.parseDouble(item.get("ALLOCATED_QTY").toString());
                double availableQty = currentQty - allocatedQty;
                
                // 가용수량 업데이트
                item.put("AVAILABLE_QTY", String.valueOf(availableQty));
                
                // 처리자 설정 (현재 로그인한 사용자 ID)
                item.put("updatedBy", userId);
                
                // 재고번호 있으면 업데이트, 없으면 신규 등록
                if (item.get("PINV_NO") != null && !item.get("PINV_NO").toString().isEmpty()) {
                    int result = pImapper.updateInventory(item);
                    if (result > 0) {
                        updateCount++;
                    }
                } else {
                    int result = pImapper.insertInventory(item);
                    if (result > 0) {
                        insertCount++;
                    }
                }
            }
			
			resultMap.put("success", true);
			resultMap.put("message", String.format("저장 완료(%d건 등록, %d건 수정)", insertCount, updateCount));
			resultMap.put("insertCount", insertCount);
			resultMap.put("updateCount", updateCount);
			
			log.info("재고 정보 저장 완료: {}건 등록, {}건 수정", insertCount, updateCount);
			
		} catch (Exception e) {
			log.error("재고 정보 저장 중 오류 발생: {}", e.getMessage(), e);
			resultMap.put("success", false);
			resultMap.put("message", "재고 정보 저장 중 오류가 발생했습니다: " + e.getMessage());
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		}
		
		return resultMap;
	}
	
	/**
     * FIFO 방식으로 재고 차감
     * 가장 오래된 출고분부터 순차적으로 재고를 차감합니다.
     * 
     * @param params 차감 정보 (pdtCode, consumptionQty, consumptionType, soCode/woCode, updatedBy 포함)
     * @return 처리 결과
     */
	@Transactional
	public Map<String, Object> consumeProductFIFO(Map<String, Object> params) {
	    Map<String, Object> resultMap = new HashMap<>();
	    
	    try {
	        String pdtCode = (String) params.get("pdtCode");
	        String consumptionQty = (String) params.get("consumptionQty");
	        
	        // 소비 타입 확인 (PRODUCTION/SHIPMENT)
	        String consumptionType = (String) params.getOrDefault("consumptionType", "PRODUCTION");
	        String soCode = (String) params.get("soCode");    // 출하용
	        String woCode = (String) params.get("woCode");    // 반제품 재투입용
	        String lotNo = (String) params.get("lotNo");
	        
	        // 현재 사용자 ID 가져오기
	        String userId = SecurityUtil.getUserId();
	        
	        log.info("FIFO 재고 차감 시작: 제품코드={}, 차감수량={}, 유형={}", 
	            pdtCode, consumptionQty, consumptionType);
	        
	        // 1. 해당 제품의 총 재고 확인
	        Map<String, Object> inventoryInfo = pImapper.getInventoryByPdtCode(pdtCode);
	        
	        if (inventoryInfo == null || 
	            Double.parseDouble((String) inventoryInfo.get("AVAILABLE_QTY")) < Double.parseDouble(consumptionQty)) {
	            log.warn("재고 부족: 요청={}, 현재재고={}", 
	                consumptionQty, 
	                inventoryInfo != null ? inventoryInfo.get("AVAILABLE_QTY") : "0");
	            resultMap.put("success", false);
	            resultMap.put("message", "재고가 부족합니다.");
	            return resultMap;
	        }
	        
	        // 2. 가장 오래된 출고분부터 차례로 조회 (FIFO)
	        List<Map<String, Object>> stockList = pIsmapper.getStocksForFIFO(pdtCode);
	        
	        log.info("FIFO 차감 대상 출고재고 조회: {}건", stockList.size());
	        
	        double remainingToConsume = Double.parseDouble(consumptionQty);
	        
	        // 3. FIFO로 재고 차감
	        for (Map<String, Object> stock : stockList) {
	            if (remainingToConsume <= 0) break;
	            
	            Long stockNo = Long.valueOf(stock.get("ISSUE_STOCK_NO").toString());
	            double remainingQty = Double.parseDouble((String) stock.get("ISSUED_QTY"));
	            
	            // 차감할 수량 결정
	            double qtyToDeduct = Math.min(remainingQty, remainingToConsume);
	            
	            log.debug("출고분 차감: 출고재고번호={}, 차감수량={}, 남은수량={}",
	                stockNo, qtyToDeduct, remainingQty - qtyToDeduct);
	            
	            // 출고별 재고 차감 처리
	            Map<String, Object> deductParams = new HashMap<>();
	            deductParams.put("issueStockNo", stockNo);
	            deductParams.put("deductQty", String.valueOf(qtyToDeduct));
	            deductParams.put("updatedBy", userId);
	            
	            pIsmapper.deductStock(deductParams);
	            
	            // FIFO 이력 저장
	            Map<String, Object> historyParams = new HashMap<>();
	            historyParams.put("issueStockNo", stockNo);
	            historyParams.put("pdtCode", pdtCode);
	            historyParams.put("consumedQty", String.valueOf(qtyToDeduct));
	            historyParams.put("consumptionType", consumptionType);
	            
	            // 소비 타입에 따라 적절한 코드 설정
	            if ("SHIPMENT".equals(consumptionType)) {
	                historyParams.put("soCode", soCode);
	            } else if ("PRODUCTION".equals(consumptionType)) {
	                historyParams.put("woCode", woCode);
	                historyParams.put("lotNo", lotNo);
	            }
	            
	            historyParams.put("consumedBy", userId);
	            historyParams.put("consumedDate", java.time.LocalDate.now().toString());
	            
	            pIsmapper.insertFIFOHistory(historyParams);
	            
	            // 차감할 남은 수량 갱신
	            remainingToConsume -= qtyToDeduct;
	        }
	        
	        // 4. 총 재고에서도 차감
	        Map<String, Object> inventoryParams = new HashMap<>();
	        inventoryParams.put("pdtCode", pdtCode);
	        inventoryParams.put("consumptionQty", consumptionQty);
	        inventoryParams.put("updatedBy", userId);
	        
	        pImapper.deductInventory(inventoryParams);
	        
	        log.info("FIFO 재고 차감 완료: 제품코드={}, 차감수량={}", pdtCode, consumptionQty);
	        
	        resultMap.put("success", true);
	        resultMap.put("message", "재고가 FIFO 원칙에 따라 성공적으로 차감되었습니다.");
	        
	    } catch (Exception e) {
	        log.error("FIFO 재고 차감 중 오류 발생: " + e.getMessage(), e);
	        resultMap.put("success", false);
	        resultMap.put("message", "재고 차감 중 오류가 발생했습니다: " + e.getMessage());
	        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
	    }
	    
	    return resultMap;
	}
	
	/**
	 * FIFO 상세 정보 조회
	 */
	public Map<String, Object> getFIFODetail(String pdtCode) {
	    Map<String, Object> result = new HashMap<>();
	    
	    // 전체 재고 정보
	    Map<String, Object> inventory = pImapper.getInventoryByPdtCode(pdtCode);
	    
	    // 출고별 재고 목록 (FIFO 순서)
	    List<Map<String, Object>> stockList = pIsmapper.getStocksForFIFO(pdtCode);
	    
	    // FIFO 순번 및 상태 추가
	    int order = 1;
	    boolean foundActive = false;
	    
	    for (Map<String, Object> stock : stockList) {
	        stock.put("FIFO_ORDER", order++);
	        
	        long issuedQty = Long.parseLong((String) stock.get("ISSUED_QTY"));
	        
	        // 상태 결정 로직
	        if (issuedQty <= 0) {
	            stock.put("STATUS", "소진");
	        } else if (!foundActive) {
	            stock.put("STATUS", "사용중");
	            foundActive = true;
	        } else {
	            stock.put("STATUS", "대기");
	        }
	    }
	    
	    result.put("INVENTORY", inventory);
	    result.put("STOCK_LIST", stockList);
	    
	    return result;
	}

	/**
	 * FIFO 이력 조회
	 */
	public List<Map<String, Object>> getFIFOHistory(String pdtCode) {
	    log.info("FIFO 이력 조회 서비스 호출. 제품코드: {}", pdtCode);
	    
	    List<Map<String, Object>> historyList = pIsmapper.getFIFOHistory(pdtCode);
	    
	    if (historyList == null) {
	        return new java.util.ArrayList<>();
	    }
	    
	    return historyList;
	}
	
	/**
	 * 제품 기본 재고 정보 자동 생성
	 * 아직 재고 정보가 없는 제품에 대해서만 기본 재고 정보 생성
	 */
	@Transactional
	public Map<String, Object> generateInitialInventoryData() {
	    Map<String, Object> resultMap = new HashMap<>();
	    int createdCount = 0;
	    List<String> failedItems = new ArrayList<>();
	    
	    try {
	        log.info("미등록 제품에 대한 기본 재고 데이터 생성 시작");
	        
	        // 모든 제품 조회 (사용 중인 것만)
	        Map<String, Object> params = new HashMap<>();
	        List<Map<String, Object>> productsList = productsMasterMapper.selectProducts(params);
	        
	        // 현재 로그인한 사용자 정보
	        String userId = SecurityUtil.getUserId();
	        
	        for (Map<String, Object> product : productsList) {
	            try {
	                String pdtCode = (String) product.get("PDT_CODE");
	                
	                // 이미 재고 정보가 존재하는지 확인
	                Map<String, Object> existingInventory = pImapper.getInventoryByPdtCode(pdtCode);
	                
	                if (existingInventory == null) {
	                    Map<String, Object> inventoryData = new HashMap<>();
	                    inventoryData.put("PDT_NO", product.get("PDT_NO"));
	                    inventoryData.put("PDT_CODE", pdtCode);
	                    inventoryData.put("WAREHOUSE_CODE", product.get("DEFAULT_WAREHOUSE_CODE"));
	                    inventoryData.put("LOCATION_CODE", product.get("DEFAULT_LOCATION_CODE"));
	                    inventoryData.put("CURRENT_QTY", "0");
	                    inventoryData.put("ALLOCATED_QTY", "0");
	                    inventoryData.put("AVAILABLE_QTY", "0"); // 트리거에 의해 자동 계산됨
	                    inventoryData.put("LAST_MOVEMENT_DATE", new java.util.Date());
	                    inventoryData.put("SAFETY_STOCK_ALERT", "N");
	                    inventoryData.put("CREATED_BY", userId);
	                    
	                    int result = pImapper.insertInventory(inventoryData);
	                    
	                    if (result > 0) {
	                        createdCount++;
	                        log.info("제품 {}에 대한 기본 재고 정보 생성 성공", pdtCode);
	                    } else {
	                        failedItems.add(pdtCode);
	                        log.warn("제품 {}에 대한 기본 재고 정보 생성 실패", pdtCode);
	                    }
	                } else {
	                    log.info("제품 {}는 이미 재고 정보가 존재합니다.", pdtCode);
	                }
	            } catch (Exception e) {
	                String pdtCode = (String) product.get("PDT_CODE");
	                failedItems.add(pdtCode);
	                log.error("제품 {} 재고 정보 생성 중 오류: {}", pdtCode, e.getMessage());
	            }
	        }
	        
	        resultMap.put("success", true);
	        resultMap.put("message", createdCount + "개의 제품에 대한 기본 재고 정보가 생성되었습니다.");
	        resultMap.put("createdCount", createdCount);
	        resultMap.put("failedItems", failedItems);
	        
	        log.info("제품 기본 재고 데이터 생성 완료: {}개 생성, {}개 실패", 
	                createdCount, failedItems.size());
	        
	    } catch (Exception e) {
	        log.error("제품 재고 데이터 생성 중 오류 발생: {}", e.getMessage(), e);
	        resultMap.put("success", false);
	        resultMap.put("message", "재고 데이터 생성 중 오류 발생: " + e.getMessage());
	        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
	    }
	    
	    return resultMap;
	}

	/**
	 * 개별 제품에 대한 기본 재고 데이터 생성
	 * 기존 재고 정보가 없는 경우에만 생성
	 */
	@Transactional
	public Map<String, Object> generateProductInventory(String pdtCode) {
	    Map<String, Object> resultMap = new HashMap<>();
	    
	    try {
	        log.info("제품 {}에 대한 기본 재고 데이터 생성 시작", pdtCode);
	        
	        // 제품 정보 조회
	        Map<String, Object> param = new HashMap<>();
	        param.put("PDT_CODE", pdtCode);
	        Map<String, Object> product = productsMasterMapper.selectSingleProducts(param);
	        
	        if (product == null) {
	            resultMap.put("success", false);
	            resultMap.put("message", "제품 정보를 찾을 수 없습니다: " + pdtCode);
	            return resultMap;
	        }
	        
	        // 이미 재고 정보가 존재하는지 확인
	        Map<String, Object> existingInventory = pImapper.getInventoryByPdtCode(pdtCode);
	        
	        if (existingInventory != null) {
	            resultMap.put("success", false);
	            resultMap.put("message", "이미 재고 정보가 존재합니다: " + pdtCode);
	            return resultMap;
	        }
	        
	        // 현재 로그인한 사용자 정보
	        String userId = SecurityUtil.getUserId();
	        
	        // 재고 정보 생성
	        Map<String, Object> inventoryData = new HashMap<>();
	        inventoryData.put("PDT_NO", product.get("PDT_NO"));
	        inventoryData.put("PDT_CODE", pdtCode);
	        inventoryData.put("WAREHOUSE_CODE", product.get("DEFAULT_WAREHOUSE_CODE"));
	        inventoryData.put("LOCATION_CODE", product.get("DEFAULT_LOCATION_CODE"));
	        inventoryData.put("CURRENT_QTY", "0");
	        inventoryData.put("ALLOCATED_QTY", "0");
	        inventoryData.put("AVAILABLE_QTY", "0"); // 트리거에 의해 자동 계산됨
	        inventoryData.put("LAST_MOVEMENT_DATE", new java.util.Date());
	        inventoryData.put("SAFETY_STOCK_ALERT", "N");
	        inventoryData.put("CREATED_BY", userId);
	        
	        int result = pImapper.insertInventory(inventoryData);
	        
	        if (result > 0) {
	            resultMap.put("success", true);
	            resultMap.put("message", "재고 정보가 생성되었습니다: " + pdtCode);
	            log.info("제품 {}에 대한 기본 재고 정보 생성 성공", pdtCode);
	        } else {
	            resultMap.put("success", false);
	            resultMap.put("message", "재고 정보 생성에 실패했습니다: " + pdtCode);
	            log.warn("제품 {}에 대한 기본 재고 정보 생성 실패", pdtCode);
	        }
	        
	    } catch (Exception e) {
	        log.error("제품 재고 데이터 생성 중 오류 발생: {}", e.getMessage(), e);
	        resultMap.put("success", false);
	        resultMap.put("message", "재고 데이터 생성 중 오류 발생: " + e.getMessage());
	        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
	    }
	    
	    return resultMap;
	}
}