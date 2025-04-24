package com.example.cmtProject.service.mes.inventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.example.cmtProject.mapper.mes.inventory.MaterialInventoryMapper;
import com.example.cmtProject.mapper.mes.inventory.MaterialReceiptStockMapper;
import com.example.cmtProject.mapper.mes.inventory.ProductsInventoryMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProductslInventoryService {
	
	@Autowired
	private ProductsInventoryMapper pImapper;
	
	@Autowired
	private MaterialReceiptStockMapper rSmapper;
	
	/**
	 * 재고 목록 조회
	 * @param map 검색 조건
	 * @return 재고 목록
	 */
	public List<Map<String,Object>> inventoryList(Map<String, Object>map){
		return pImapper.pInventoryList(map);
	}
	
//	/**
//	 * 재고 정보 저장
//	 * @param inventoryList 저장할 재고 목록
//	 * @return 처리 결과
//	 */
//	@Transactional
//	public Map<String, Object> saveInventory(List<Map<String, Object>> inventoryList) {
//		Map<String, Object> resultMap = new HashMap<>();
//		int updateCount = 0;
//		int insertCount = 0;
//		
//		try {
//			log.info("재고 정보 저장 시작: {}건", inventoryList.size());
//			
//			for (Map<String, Object> item : inventoryList) {
//				// 계산된 가용수량 설정 (가용수량 = 현재수량 - 할당수량)
//				double currentQty = Double.parseDouble(item.get("CURRENT_QTY").toString());
//				double allocatedQty = Double.parseDouble(item.get("ALLOCATED_QTY").toString());
//				double availableQty = currentQty - allocatedQty;
//				
//				// 가용수량 업데이트
//				item.put("AVAILABLE_QTY", String.valueOf(availableQty));
//				
//				// 처리자 설정 (사용자 정보나 시스템 정보에서 추출)
//				item.put("updatedBy", item.getOrDefault("updatedBy", "SYSTEM"));
//				
//				// 재고번호 있으면 업데이트, 없으면 신규 등록
//				if (item.get("INV_NO") != null && !item.get("INV_NO").toString().isEmpty()) {
//					int result = mImapper.updateInventory(item);
//					if (result > 0) {
//						updateCount++;
//					}
//				} else {
//					int result = mImapper.insertInventory(item);
//					if (result > 0) {
//						insertCount++;
//					}
//				}
//			}
//			
//			resultMap.put("success", true);
//			resultMap.put("message", String.format("저장 완료(%d건 등록, %d건 수정)", insertCount, updateCount));
//			resultMap.put("insertCount", insertCount);
//			resultMap.put("updateCount", updateCount);
//			
//			log.info("재고 정보 저장 완료: {}건 등록, {}건 수정", insertCount, updateCount);
//			
//		} catch (Exception e) {
//			log.error("재고 정보 저장 중 오류 발생: {}", e.getMessage(), e);
//			resultMap.put("success", false);
//			resultMap.put("message", "재고 정보 저장 중 오류가 발생했습니다: " + e.getMessage());
//			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//		}
//		
//		return resultMap;
//	}
//	
//	/**
//     * FIFO 방식으로 재고 차감
//     * 가장 오래된 입고분부터 순차적으로 재고를 차감합니다.
//     * 
//     * @param params 차감 정보 (mtlCode, consumptionQty, updatedBy 포함)
//     * @return 처리 결과
//     */
//    @Transactional
//    public Map<String, Object> consumeMaterialFIFO(Map<String, Object> params) {
//        Map<String, Object> resultMap = new HashMap<>();
//        
//        try {
//            String mtlCode = (String) params.get("mtlCode");
//            String consumptionQty = (String) params.get("consumptionQty");
//            
//            log.info("FIFO 재고 차감 시작: 자재코드={}, 차감수량={}", mtlCode, consumptionQty);
//            
//            // 1. 해당 자재의 총 재고 확인
//            Map<String, Object> inventoryInfo = mImapper.getInventoryByMtlCode(mtlCode);
//            
//            if (inventoryInfo == null || 
//                Double.parseDouble((String) inventoryInfo.get("CURRENT_QTY")) < Double.parseDouble(consumptionQty)) {
//                log.warn("재고 부족: 요청={}, 현재재고={}", 
//                    consumptionQty, 
//                    inventoryInfo != null ? inventoryInfo.get("CURRENT_QTY") : "0");
//                resultMap.put("success", false);
//                resultMap.put("message", "재고가 부족합니다.");
//                return resultMap;
//            }
//            
//            // 2. 가장 오래된 입고분부터 차례로 조회
//            List<Map<String, Object>> stockList = 
//            		rSmapper.getStocksByMtlCodeOrderByDate(mtlCode);
//            
//            log.info("FIFO 차감 대상 입고재고 조회: {}건", stockList.size());
//            
//            double remainingToConsume = Double.parseDouble(consumptionQty);
//            
//            // 3. FIFO로 재고 차감
//            for (Map<String, Object> stock : stockList) {
//                if (remainingToConsume <= 0) break;
//                
//                Long stockNo = Long.valueOf(stock.get("RECEIPT_STOCK_NO").toString());
//                double remainingQty = Double.parseDouble((String) stock.get("REMAINING_QTY"));
//                
//                // 차감할 수량 결정
//                double qtyToDeduct = Math.min(remainingQty, remainingToConsume);
//                
//                log.debug("입고분 차감: 입고재고번호={}, 차감수량={}, 남은수량={}",
//                    stockNo, qtyToDeduct, remainingQty - qtyToDeduct);
//                
//                // 입고별 재고 차감
//                Map<String, Object> deductParams = new HashMap<>();
//                deductParams.put("receiptStockNo", stockNo);
//                deductParams.put("deductQty", String.valueOf(qtyToDeduct));
//                deductParams.put("updatedBy", params.get("updatedBy"));
//                
//                rSmapper.deductStock(deductParams);
//                
//                // 차감할 남은 수량 갱신
//                remainingToConsume -= qtyToDeduct;
//            }
//            
//            // 4. 총 재고에서도 차감
//            Map<String, Object> inventoryParams = new HashMap<>();
//            inventoryParams.put("mtlCode", mtlCode);
//            inventoryParams.put("consumptionQty", consumptionQty);
//            inventoryParams.put("updatedBy", params.get("updatedBy"));
//            
//            mImapper.deductInventory(inventoryParams);
//            
//            log.info("FIFO 재고 차감 완료: 자재코드={}, 차감수량={}", mtlCode, consumptionQty);
//            
//            resultMap.put("success", true);
//            resultMap.put("message", "재고가 FIFO 원칙에 따라 성공적으로 차감되었습니다.");
//            
//        } catch (Exception e) {
//            log.error("FIFO 재고 차감 중 오류 발생: " + e.getMessage(), e);
//            resultMap.put("success", false);
//            resultMap.put("message", "재고 차감 중 오류가 발생했습니다: " + e.getMessage());
//            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//        }
//        
//        return resultMap;
//    }
}