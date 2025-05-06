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

import com.example.cmtProject.mapper.mes.inventory.ProductsInventoryMapper;
import com.example.cmtProject.mapper.mes.inventory.ProductsIssueStockMapper;
import com.example.cmtProject.mapper.mes.inventory.ProductsMasterMapper;
import com.example.cmtProject.mapper.mes.inventory.ProductsProductionReceiptMapper;
import com.example.cmtProject.mapper.mes.inventory.ProductsProductionReceiptStockMapper;
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
	
	@Autowired
	private ProductsProductionReceiptStockMapper pprsmapper;
	
	@Autowired
	private ProductsProductionReceiptMapper pprMapper;
	
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
                long currentQty = Long.parseLong(item.get("CURRENT_QTY").toString());
                long allocatedQty = Long.parseLong(item.get("ALLOCATED_QTY").toString());
                long availableQty = currentQty - allocatedQty;
                
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
	 * FIFO 방식으로 재고 차감 및 이력 기록
	 * 통합된 재고 차감 메서드 - 모든 재고 차감은 이 메서드를 통해 이루어져야 함
	 * 
	 * @param params 차감 파라미터 (pdtCode, consumptionQty, consumptionType, lotNo, woCode, soCode, updatedBy)
	 * @return 처리 결과
	 */
	@Transactional
	public Map<String, Object> consumeProductFIFO(Map<String, Object> params) {
	    Map<String, Object> resultMap = new HashMap<>();
	    
	    try {
	        String pdtCode = (String) params.get("pdtCode");
	        String consumptionQtyStr = (String) params.get("consumptionQty");
	        long consumptionQty = Long.parseLong(consumptionQtyStr);
	        
	        // 소비 타입 확인 (PRODUCTION/SHIPMENT)
	        String consumptionType = (String) params.getOrDefault("consumptionType", "PRODUCTION");
	        String soCode = (String) params.get("soCode");    // 출하용
	        String woCode = (String) params.get("woCode");    // 반제품 재투입용
	        String lotNo = (String) params.get("lotNo");
	        
	        // 현재 사용자 ID 가져오기
	        String userId = (String) params.getOrDefault("updatedBy", SecurityUtil.getUserId());
	        
	        log.info("FIFO 재고 차감 시작: 제품코드={}, 차감수량={}, 유형={}", 
	            pdtCode, consumptionQty, consumptionType);
	        
	        // 1. 해당 제품의 총 재고 확인
	        Map<String, Object> inventoryInfo = pImapper.getInventoryByPdtCode(pdtCode);
	        
	        if (inventoryInfo == null || 
	            Long.parseLong((String) inventoryInfo.get("AVAILABLE_QTY")) < consumptionQty) {
	            log.warn("재고 부족: 요청={}, 현재재고={}", 
	                consumptionQty, 
	                inventoryInfo != null ? inventoryInfo.get("AVAILABLE_QTY") : "0");
	            resultMap.put("success", false);
	            resultMap.put("message", "재고가 부족합니다.");
	            return resultMap;
	        }
	        
	        // 2. 가장 오래된 생산입고분부터 차례로 조회 (FIFO)
	        List<Map<String, Object>> stockList = pprsmapper.getStocksForFIFO(pdtCode);
	        
	        log.info("FIFO 차감 대상 생산입고재고 조회: {}건", stockList.size());
	        
	        long remainingToConsume = consumptionQty;
	        List<Map<String, Object>> consumptionDetails = new ArrayList<>();
	        
	        // 3. FIFO로 재고 차감
	        for (Map<String, Object> stock : stockList) {
	            if (remainingToConsume <= 0) break;
	            
	            Long stockNo = Long.valueOf(stock.get("PRODUCTION_RECEIPT_STOCK_NO").toString());
	            long remainingQty = Long.parseLong((String) stock.get("REMAINING_QTY"));
	            
	            // 차감할 수량 결정
	            long qtyToDeduct = Math.min(remainingQty, remainingToConsume);
	            
	            log.debug("생산입고분 차감: 입고재고번호={}, 차감수량={}, 남은수량={}",
	                stockNo, qtyToDeduct, remainingQty - qtyToDeduct);
	            
	            // 생산입고별 재고 차감 처리
	            Map<String, Object> deductParams = new HashMap<>();
	            deductParams.put("productionReceiptStockNo", stockNo);
	            deductParams.put("deductQty", String.valueOf(qtyToDeduct));
	            deductParams.put("updatedBy", userId);
	            
	            pprsmapper.deductStock(deductParams);
	            
	            // FIFO 이력 저장
	            try {
	                Map<String, Object> historyParams = new HashMap<>();
	                historyParams.put("issueStockNo", stockNo);  // issue_stock_no 대신 production_receipt_stock_no 사용
	                historyParams.put("pdtCode", pdtCode);
	                historyParams.put("consumedQty", String.valueOf(qtyToDeduct));
	                historyParams.put("consumptionType", consumptionType);
	                
	                // 로트번호, 수주코드, 작업지시코드 설정
	                if (lotNo != null && !lotNo.isEmpty()) {
	                    historyParams.put("lotNo", lotNo);
	                }
	                
	                if ("SHIPMENT".equals(consumptionType) && soCode != null && !soCode.isEmpty()) {
	                    historyParams.put("soCode", soCode);
	                } else if ("PRODUCTION".equals(consumptionType) && woCode != null && !woCode.isEmpty()) {
	                    historyParams.put("woCode", woCode);
	                }
	                
	                historyParams.put("consumedBy", userId);
	                historyParams.put("consumedDate", java.time.LocalDate.now().toString());
	                
	                // 이력 저장 실행
	                int historyResult = pIsmapper.insertFIFOHistory(historyParams);
	                
	                // 이력 저장 결과 로깅
	                log.info("FIFO 이력 저장 결과: {} (1=성공)", historyResult);
	                
	                // 차감 상세 정보 저장
	                Map<String, Object> consumptionDetail = new HashMap<>();
	                consumptionDetail.put("stockNo", stockNo);
	                consumptionDetail.put("deductedQty", qtyToDeduct);
	                consumptionDetail.put("historyResult", historyResult);
	                consumptionDetails.add(consumptionDetail);
	            } catch (Exception historyException) {
	                log.error("FIFO 이력 저장 중 오류 발생: {}", historyException.getMessage(), historyException);
	                // 이력 저장 실패해도 재고 차감은 계속 진행
	            }
	            
	            // 차감할 남은 수량 갱신
	            remainingToConsume -= qtyToDeduct;
	        }
	        
	        // 4. 총 재고에서도 차감
	        Map<String, Object> inventoryParams = new HashMap<>();
	        inventoryParams.put("pdtCode", pdtCode);
	        inventoryParams.put("consumptionQty", consumptionQtyStr);
	        inventoryParams.put("updatedBy", userId);
	        
	        pImapper.deductInventory(inventoryParams);
	        
	        log.info("FIFO 재고 차감 완료: 제품코드={}, 차감수량={}", pdtCode, consumptionQty);
	        
	        resultMap.put("success", true);
	        resultMap.put("message", "재고가 FIFO 원칙에 따라 성공적으로 차감되었습니다.");
	        resultMap.put("consumptionDetails", consumptionDetails);
	        
	    } catch (Exception e) {
	        log.error("FIFO 재고 차감 중 오류 발생: " + e.getMessage(), e);
	        resultMap.put("success", false);
	        resultMap.put("message", "재고 차감 중 오류가 발생했습니다: " + e.getMessage());
	        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
	    }
	    
	    return resultMap;
	}
	
	/**
	 * 생산 공정에서 사용하는 제품 재고 차감 메서드
	 * 내부적으로 consumeProductFIFO를 호출하여 일관된 차감 처리
	 * 
	 * @param params 생산 공정 관련 파라미터
	 * @return 처리 결과
	 */
	@Transactional
	public Map<String, Object> consumeProductByProduction(Map<String, Object> params) {
	    String pdtCode = (String) params.get("parentPdtCode");
	    String qtyStr = (String) params.get("bomQty");
	    String lotNo = (String) params.get("childLotCode");
	    String woCode = (String) params.get("woCode");
	    String userId = (String) params.getOrDefault("userId", SecurityUtil.getUserId());
	    
	    log.info("생산 공정 차감 시작: 제품코드={}, 수량={}, 작업지시={}, 로트={}",
	        pdtCode, qtyStr, woCode, lotNo);
	    
	    Map<String, Object> fifoParams = new HashMap<>();
	    fifoParams.put("pdtCode", pdtCode);
	    fifoParams.put("consumptionQty", qtyStr);
	    fifoParams.put("consumptionType", "PRODUCTION");
	    fifoParams.put("lotNo", lotNo);
	    fifoParams.put("woCode", woCode);
	    fifoParams.put("updatedBy", userId);
	    
	    return consumeProductFIFO(fifoParams);
	}	     
	
	/**
	 * FIFO 상세 정보 조회
	 * 생산공정을 통한 입고 데이터를 기반으로 FIFO 정보 제공
	 */
	public Map<String, Object> getFIFODetail(String pdtCode) {
	    Map<String, Object> result = new HashMap<>();
	    
	    try {
	        log.info("제품 FIFO 상세 정보 조회 시작: pdtCode={}", pdtCode);
	        
	        // 전체 재고 정보
	        Map<String, Object> inventory = pImapper.getInventoryByPdtCode(pdtCode);
	        
	        // 생산 입고 재고 목록 조회 (FIFO 순서)
	        List<Map<String, Object>> stockList = pprsmapper.getStocksForFIFO(pdtCode);
	        log.info("생산 입고 FIFO 목록 조회 결과: {}건", stockList != null ? stockList.size() : 0);
	        
	        if (stockList == null) {
	            stockList = new ArrayList<>();
	        }
	        
	        // FIFO 순번 및 상태 추가
	        int order = 1;
	        boolean foundActive = false;
	        
	        for (Map<String, Object> stock : stockList) {
	            // FIFO 순번 설정
	            stock.put("FIFO_ORDER", order++);
	            
	            // 남은 수량 확인
	            long remainingQty = Long.parseLong((String) stock.get("REMAINING_QTY"));
	            
	            // 상태 결정 로직
	            if (remainingQty <= 0) {
	                stock.put("STATUS", "소진");
	            } else if (!foundActive) {
	                stock.put("STATUS", "사용중");
	                foundActive = true;
	            } else {
	                stock.put("STATUS", "대기");
	            }
	            
	            // 프론트엔드 호환을 위한 필드 매핑
	            stock.put("ISSUE_NO", stock.get("PRODUCTION_CODE"));
	            stock.put("ISSUE_DATE", stock.get("PRODUCTION_DATE"));
	            stock.put("ISSUED_QTY", stock.get("REMAINING_QTY"));
	            // ORIGINAL_QTY는 이미 JOIN으로 설정되어 있음
	        }
	        
	        result.put("INVENTORY", inventory);
	        result.put("STOCK_LIST", stockList);
	        
	        log.info("제품 FIFO 상세 정보 조회 완료");
	        
	    } catch (Exception e) {
	        log.error("제품 FIFO 상세 정보 조회 중 오류: {}", e.getMessage(), e);
	        result.put("INVENTORY", null);
	        result.put("STOCK_LIST", new ArrayList<>());
	    }
	    
	    return result;
	}
	
	/**
	 * FIFO 이력 조회
	 */
	public List<Map<String, Object>> getFIFOHistory(String pdtCode) {
	    log.info("FIFO 이력 조회 서비스 호출. 제품코드: {}", pdtCode);
	    
	    List<Map<String, Object>> historyList = pIsmapper.getFIFOHistory(pdtCode);
	    
	    if (historyList == null) {
	        return new ArrayList<>();
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
	/**
	 * 임시 생산입고 처리 (초기 재고 데이터 생성용)
	 * FIFO 관리를 위한 생산입고 이력 생성 및 재고 증가 처리
	 */
	@Transactional
	public Map<String, Object> createTempProductionReceipt(Map<String, Object> params) {
	    Map<String, Object> resultMap = new HashMap<>();
	    
	    try {
	        log.info("임시 생산입고 처리 시작: {}", params);
	        
	        // 필수 파라미터 검증
	        if (!params.containsKey("pdtCode") || !params.containsKey("qty")) {
	            resultMap.put("success", false);
	            resultMap.put("message", "필수 파라미터가 누락되었습니다. (pdtCode, qty)");
	            return resultMap;
	        }
	        
	        String pdtCode = (String) params.get("pdtCode");
	        String qty = (String) params.get("qty");
	        String lotNo = (String) params.getOrDefault("lotNo", "INIT-" + pdtCode + "-" + System.currentTimeMillis());
	        
	        // 제품 정보 조회
	        Map<String, Object> param = new HashMap<>();
	        param.put("PDT_CODE", pdtCode);
	        Map<String, Object> productInfo = productsMasterMapper.selectSingleProducts(param);
	        
	        if (productInfo == null) {
	            resultMap.put("success", false);
	            resultMap.put("message", "제품 정보를 찾을 수 없습니다: " + pdtCode);
	            return resultMap;
	        }
	        
	        // 현재 사용자 ID 가져오기
	        String userId = SecurityUtil.getUserId();
	        
	        // 1. 생산입고 정보 생성
	        String receiptCode = "PR-INIT-" + pdtCode + "-" + System.currentTimeMillis();
	        
	        Map<String, Object> receiptParams = new HashMap<>();
	        receiptParams.put("receiptCode", receiptCode);
	        receiptParams.put("productionCode", lotNo);
	        receiptParams.put("pdtCode", pdtCode);
	        receiptParams.put("receivedQty", qty);
	        receiptParams.put("receiptDate", java.time.LocalDate.now().toString());
	        receiptParams.put("receiptStatus", "입고완료");
	        receiptParams.put("warehouseCode", productInfo.get("DEFAULT_WAREHOUSE_CODE"));
	        receiptParams.put("locationCode", productInfo.get("DEFAULT_LOCATION_CODE"));
	        receiptParams.put("receiver", userId);
	        receiptParams.put("createdBy", userId);
	        
	        // ProductsProductionReceiptMapper를 통한 데이터 저장
	        pprMapper.insertProductionReceipt(receiptParams);
	        Long receiptNo = pprMapper.getLastReceiptNo();
	        
	        // 2. FIFO 관리용 생산입고 이력 저장
	        Map<String, Object> stockParams = new HashMap<>();
	        stockParams.put("receiptNo", receiptNo);
	        stockParams.put("productionCode", lotNo);
	        stockParams.put("pdtCode", pdtCode);
	        stockParams.put("remainingQty", qty);
	        stockParams.put("productionDate", java.time.LocalDate.now().toString());
	        stockParams.put("lotNo", lotNo);
	        stockParams.put("createdBy", userId);
	        
	        pprsmapper.insertStock(stockParams);
	        
	        // 3. 제품 재고 증가
	        Map<String, Object> inventoryParams = new HashMap<>();
	        inventoryParams.put("pdtCode", pdtCode);
	        inventoryParams.put("receivedQty", qty);
	        inventoryParams.put("warehouseCode", productInfo.get("DEFAULT_WAREHOUSE_CODE"));
	        inventoryParams.put("locationCode", productInfo.get("DEFAULT_LOCATION_CODE"));
	        inventoryParams.put("updatedBy", userId);
	        
	        pImapper.mergeInventory(inventoryParams);
	        
	        resultMap.put("success", true);
	        resultMap.put("message", "임시 생산입고 처리가 완료되었습니다.");
	        resultMap.put("receiptNo", receiptNo);
	        resultMap.put("pdtCode", pdtCode);
	        resultMap.put("qty", qty);
	        resultMap.put("lotNo", lotNo);
	        
	        log.info("임시 생산입고 처리 완료: 제품코드={}, 수량={}, LOT={}", pdtCode, qty, lotNo);
	        
	    } catch (Exception e) {
	        log.error("임시 생산입고 처리 중 오류 발생: {}", e.getMessage(), e);
	        resultMap.put("success", false);
	        resultMap.put("message", "오류가 발생했습니다: " + e.getMessage());
	        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
	    }
	    
	    return resultMap;
	}
	
	/**
	 * 모든 제품에 대한 임시 생산입고 처리 (초기 재고 데이터 일괄 생성)
	 * 
	 * @param params 입력 파라미터 (기본 수량, 창고/위치 코드 등)
	 * @return 처리 결과
	 */
	@Transactional
	public Map<String, Object> createTempProductionReceiptForAll(Map<String, Object> params) {
	    Map<String, Object> resultMap = new HashMap<>();
	    int successCount = 0;
	    int failCount = 0;
	    List<String> failedItems = new ArrayList<>();
	    
	    try {
	        log.info("전체 제품 임시 생산입고 처리 시작: {}", params);
	        
	        // 필수 파라미터 검증
	        if (!params.containsKey("defaultQty")) {
	            resultMap.put("success", false);
	            resultMap.put("message", "기본 수량은 필수입니다.");
	            return resultMap;
	        }
	        
	        String defaultQty = (String) params.get("defaultQty");
	        
	        // 제품 목록 조회 (사용 중인 것만)
	        Map<String, Object> queryParams = new HashMap<>();
	        List<Map<String, Object>> productsList = productsMasterMapper.selectProducts(queryParams);
	        
	        if (productsList.isEmpty()) {
	            resultMap.put("success", false);
	            resultMap.put("message", "처리할 제품이 없습니다.");
	            return resultMap;
	        }
	        
	        log.info("처리할 제품 수: {}", productsList.size());
	        
	        // 현재 사용자 ID 가져오기
	        String userId = SecurityUtil.getUserId();
	        
	        // 각 제품에 대해 임시 생산입고 처리
	        for (Map<String, Object> product : productsList) {
	            try {
	                String pdtCode = (String) product.get("PDT_CODE");
	                
	                // 이미 재고가 있는지 확인
	                Map<String, Object> inventory = pImapper.getInventoryByPdtCode(pdtCode);
	                
	                // 재고가 있고 임시 데이터 생성 옵션이 false인 경우 스킵
	                boolean overwriteExisting = params.containsKey("overwriteExisting") && 
	                                         (Boolean) params.get("overwriteExisting");
	                
	             // 재고가 있고(재고 수량이 0보다 큰 경우) 임시 데이터 생성 옵션이 false인 경우 스킵
	                if (inventory != null && Integer.parseInt(inventory.get("CURRENT_QTY").toString()) > 0 && !overwriteExisting) {
	                    log.info("제품 {}은(는) 이미 재고가 있어 스킵합니다.", pdtCode);
	                    continue;
	                }
	                // LOT 번호 생성
	                String lotNo = "INIT-" + pdtCode + "-" + System.currentTimeMillis();
	                
	                // 임시 생산입고 처리 파라미터 설정
	                Map<String, Object> entryParams = new HashMap<>();
	                entryParams.put("pdtCode", pdtCode);
	                entryParams.put("qty", defaultQty);
	                entryParams.put("lotNo", lotNo);
	                
	                // 개별 임시 생산입고 처리 호출
	                Map<String, Object> result = createTempProductionReceipt(entryParams);
	                
	                if ((Boolean) result.get("success")) {
	                    successCount++;
	                    log.info("제품 {} 임시 생산입고 성공: {}", pdtCode, result.get("message"));
	                } else {
	                    failCount++;
	                    failedItems.add(pdtCode);
	                    log.warn("제품 {} 임시 생산입고 실패: {}", pdtCode, result.get("message"));
	                }
	                
	                // 처리 간격을 두어 시스템 부하 감소
	                Thread.sleep(10);
	                
	            } catch (Exception e) {
	                failCount++;
	                failedItems.add((String) product.get("PDT_CODE"));
	                log.error("제품 {} 처리 중 오류: {}", product.get("PDT_CODE"), e.getMessage(), e);
	            }
	        }
	        
	        if (successCount > 0) {
	            String message = successCount + "개 제품의 임시 생산입고가 완료되었습니다.";
	            if (failCount > 0) {
	                message += " (" + failCount + "개 실패)";
	            }
	            
	            resultMap.put("success", true);
	            resultMap.put("message", message);
	        } else {
	            resultMap.put("success", false);
	            resultMap.put("message", "모든 제품의 임시 생산입고 처리에 실패했습니다.");
	        }
	        
	        resultMap.put("successCount", successCount);
	        resultMap.put("failCount", failCount);
	        resultMap.put("failedItems", failedItems);
	        
	        log.info("전체 제품 임시 생산입고 처리 완료: 성공={}, 실패={}", successCount, failCount);
	        
	    } catch (Exception e) {
	        log.error("전체 제품 임시 생산입고 처리 중 오류: {}", e.getMessage(), e);
	        resultMap.put("success", false);
	        resultMap.put("message", "처리 중 오류가 발생했습니다: " + e.getMessage());
	        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
	    }
	    
	    return resultMap;
	}
}