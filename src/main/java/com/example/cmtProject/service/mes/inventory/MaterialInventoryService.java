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

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MaterialInventoryService {
	
	@Autowired
	private MaterialInventoryMapper mImapper;
	
	@Autowired
	private MaterialReceiptStockMapper rSmapper;
	
	
	
	/**
	 * 재고 목록 조회
	 * @param map 검색 조건
	 * @return 재고 목록
	 */
	public List<Map<String,Object>> inventoryList(Map<String, Object>map){
		
		return mImapper.mInventoryList(map);
		
	}
	
	
	/**
     * FIFO 방식으로 재고 차감
     * 가장 오래된 입고분부터 순차적으로 재고를 차감합니다.
     * 
     * @param params 차감 정보 (mtlCode, consumptionQty, updatedBy 포함)
     * @return 처리 결과
     */
    @Transactional
    public Map<String, Object> consumeMaterialFIFO(Map<String, Object> params) {
        Map<String, Object> resultMap = new HashMap<>();
        
        try {
            String mtlCode = (String) params.get("mtlCode");
            String consumptionQty = (String) params.get("consumptionQty");
            
            log.info("FIFO 재고 차감 시작: 자재코드={}, 차감수량={}", mtlCode, consumptionQty);
            
            // 1. 해당 자재의 총 재고 확인
            Map<String, Object> inventoryInfo = mImapper.getInventoryByMtlCode(mtlCode);
            
            if (inventoryInfo == null || 
                Double.parseDouble((String) inventoryInfo.get("CURRENT_QTY")) < Double.parseDouble(consumptionQty)) {
                log.warn("재고 부족: 요청={}, 현재재고={}", 
                    consumptionQty, 
                    inventoryInfo != null ? inventoryInfo.get("CURRENT_QTY") : "0");
                resultMap.put("success", false);
                resultMap.put("message", "재고가 부족합니다.");
                return resultMap;
            }
            
            // 2. 가장 오래된 입고분부터 차례로 조회
            List<Map<String, Object>> stockList = 
            		rSmapper.getStocksByMtlCodeOrderByDate(mtlCode);
            
            log.info("FIFO 차감 대상 입고재고 조회: {}건", stockList.size());
            
            double remainingToConsume = Double.parseDouble(consumptionQty);
            
            // 3. FIFO로 재고 차감
            for (Map<String, Object> stock : stockList) {
                if (remainingToConsume <= 0) break;
                
                Long stockNo = Long.valueOf(stock.get("RECEIPT_STOCK_NO").toString());
                double remainingQty = Double.parseDouble((String) stock.get("REMAINING_QTY"));
                
                // 차감할 수량 결정
                double qtyToDeduct = Math.min(remainingQty, remainingToConsume);
                
                log.debug("입고분 차감: 입고재고번호={}, 차감수량={}, 남은수량={}",
                    stockNo, qtyToDeduct, remainingQty - qtyToDeduct);
                
                // 입고별 재고 차감
                Map<String, Object> deductParams = new HashMap<>();
                deductParams.put("receiptStockNo", stockNo);
                deductParams.put("deductQty", String.valueOf(qtyToDeduct));
                deductParams.put("updatedBy", params.get("updatedBy"));
                
                rSmapper.deductStock(deductParams);
                
                // 차감할 남은 수량 갱신
                remainingToConsume -= qtyToDeduct;
            }
            
            // 4. 총 재고에서도 차감
            Map<String, Object> inventoryParams = new HashMap<>();
            inventoryParams.put("mtlCode", mtlCode);
            inventoryParams.put("consumptionQty", consumptionQty);
            inventoryParams.put("updatedBy", params.get("updatedBy"));
            
            mImapper.deductInventory(inventoryParams);
            
            log.info("FIFO 재고 차감 완료: 자재코드={}, 차감수량={}", mtlCode, consumptionQty);
            
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

}
