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
import com.example.cmtProject.mapper.mes.inventory.MaterialReceiptStockMapper;
import com.example.cmtProject.util.SecurityUtil;

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
     */
    public List<Map<String,Object>> inventoryList(Map<String, Object>map){
        return mImapper.mInventoryList(map);
    }
    
    /**
     * 재고 정보 저장
     */
    @Transactional
    public Map<String, Object> saveInventory(List<Map<String, Object>> inventoryList) {
        Map<String, Object> resultMap = new HashMap<>();
        int updateCount = 0;
        int insertCount = 0;
        
        try {
            log.info("재고 정보 저장 시작: {}건", inventoryList.size());
            
            String userId = SecurityUtil.getUserId();
            
            for (Map<String, Object> item : inventoryList) {
                item.put("updatedBy", userId);
                
                if (item.get("INV_NO") != null && !item.get("INV_NO").toString().isEmpty()) {
                    int result = mImapper.updateInventory(item);
                    if (result > 0) {
                        updateCount++;
                    }
                } else {
                    int result = mImapper.insertInventory(item);
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
     */
    @Transactional
    public Map<String, Object> consumeMaterialFIFO(Map<String, Object> params) {
        Map<String, Object> resultMap = new HashMap<>();
        
        try {
            String mtlCode = (String) params.get("mtlCode");
            String consumptionQty = (String) params.get("consumptionQty");
            
            // LOT 정보 추출 (있는 경우)
            String lotNo = (String) params.get("lotNo");
            String woCode = (String) params.get("woCode");
            
            String userId = SecurityUtil.getUserId();
            
            log.info("FIFO 재고 차감 시작: 자재코드={}, 차감수량={}", mtlCode, consumptionQty);
            
            // 1. 해당 자재의 총 재고 확인
            Map<String, Object> inventoryInfo = mImapper.getInventoryByMtlCode(mtlCode);
            
            if (inventoryInfo == null || 
                Double.parseDouble((String) inventoryInfo.get("CURRENT_QTY")) < Double.parseDouble(consumptionQty)) {
                resultMap.put("success", false);
                resultMap.put("message", "재고가 부족합니다.");
                return resultMap;
            }
            
            // 2. 가장 오래된 입고분부터 차례로 조회
            List<Map<String, Object>> stockList = rSmapper.getStocksByMtlCodeOrderByDate(mtlCode);
            
            double remainingToConsume = Double.parseDouble(consumptionQty);
            List<Map<String, Object>> consumptionDetails = new ArrayList<>();
            
            // 3. FIFO로 재고 차감 및 이력 기록
            for (Map<String, Object> stock : stockList) {
                if (remainingToConsume <= 0) break;
                
                Long stockNo = Long.valueOf(stock.get("RECEIPT_STOCK_NO").toString());
                double remainingQty = Double.parseDouble((String) stock.get("REMAINING_QTY"));
                
                // 차감할 수량 결정
                double qtyToDeduct = Math.min(remainingQty, remainingToConsume);
                
                // 입고별 재고 차감
                Map<String, Object> deductParams = new HashMap<>();
                deductParams.put("receiptStockNo", stockNo);
                deductParams.put("deductQty", String.valueOf(qtyToDeduct));
                deductParams.put("updatedBy", userId);
                
                rSmapper.deductStock(deductParams);
                
                // FIFO 이력 저장 (LOT와 WO 정보가 있는 경우만)
                if (lotNo != null && woCode != null) {
                    Map<String, Object> historyParams = new HashMap<>();
                    historyParams.put("receiptStockNo", stockNo);
                    historyParams.put("mtlCode", mtlCode);
                    historyParams.put("consumedQty", String.valueOf(qtyToDeduct));
                    historyParams.put("lotNo", lotNo);
                    historyParams.put("woCode", woCode);
                    historyParams.put("consumedBy", userId);
                    historyParams.put("consumedDate", LocalDate.now().toString());
                    
                    rSmapper.insertFIFOHistory(historyParams);
                }
                
                // 차감 상세 정보 저장 (확장용)
                Map<String, Object> consumptionDetail = new HashMap<>();
                consumptionDetail.put("receiptNo", stock.get("RECEIPT_NO"));
                consumptionDetail.put("receiptDate", stock.get("RECEIPT_DATE"));
                consumptionDetail.put("consumedQty", qtyToDeduct);
                consumptionDetail.put("remainingQty", remainingQty - qtyToDeduct);
                consumptionDetail.put("timestamp", java.time.LocalDateTime.now().toString());
                consumptionDetails.add(consumptionDetail);
                
                remainingToConsume -= qtyToDeduct;
            }
            
            // 4. 총 재고에서도 차감
            Map<String, Object> inventoryParams = new HashMap<>();
            inventoryParams.put("mtlCode", mtlCode);
            inventoryParams.put("consumptionQty", consumptionQty);
            inventoryParams.put("updatedBy", userId);
            
            mImapper.deductInventory(inventoryParams);
            
            log.info("FIFO 재고 차감 완료: 자재코드={}, 차감수량={}", mtlCode, consumptionQty);
            
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
     * FIFO 상세 정보 조회
     */
    public Map<String, Object> getFIFODetail(String mtlCode) {
        Map<String, Object> result = new HashMap<>();
        
        // 전체 재고 정보
        Map<String, Object> inventory = mImapper.getInventoryByMtlCode(mtlCode);
        
        // 입고별 재고 목록 (FIFO 순서)
        List<Map<String, Object>> stockList = rSmapper.getStocksByMtlCodeOrderByDate(mtlCode);
        
        // FIFO 순번 및 상태 추가
        int order = 1;
        boolean foundActive = false;
        
        for (Map<String, Object> stock : stockList) {
            stock.put("FIFO_ORDER", order++);
            
            long originalQty = Long.parseLong((String) stock.get("ORIGINAL_QTY"));
            long remainingQty = Long.parseLong((String) stock.get("REMAINING_QTY"));
            
            // 상태 결정 로직
            if (remainingQty <= 0) {
                stock.put("STATUS", "소진");
            } else if (remainingQty < originalQty && !foundActive) {
                stock.put("STATUS", "사용중");
                foundActive = true;
            } else if (!foundActive && order == 2) {
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
    public List<Map<String, Object>> getFIFOHistory(String mtlCode) {
        log.info("FIFO 이력 조회: 자재코드={}", mtlCode);
        
        List<Map<String, Object>> historyList = rSmapper.getFIFOHistory(mtlCode);
        
        if (historyList == null || historyList.isEmpty()) {
            log.info("자재 {}의 FIFO 이력이 없습니다.", mtlCode);
            return new ArrayList<>();
        }
        
        log.info("FIFO 이력 조회 완료: {}건", historyList.size());
        return historyList;
    }

    /**
     * FIFO 상태 통계 조회 (확장용)
     */
    public Map<String, Object> getFIFOStatistics(String mtlCode) {
        Map<String, Object> stats = new HashMap<>();
        
        List<Map<String, Object>> stockList = rSmapper.getStocksByMtlCodeOrderByDate(mtlCode);
        
        int activeCount = 0;
        int waitingCount = 0;
        int consumedCount = 0;
        
        for (Map<String, Object> stock : stockList) {
            double remaining = Double.parseDouble((String) stock.get("REMAINING_QTY"));
            if (remaining > 0) {
                if (activeCount == 0) activeCount++;
                else waitingCount++;
            } else {
                consumedCount++;
            }
        }
        
        stats.put("activeCount", activeCount);
        stats.put("waitingCount", waitingCount);
        stats.put("consumedCount", consumedCount);
        
        return stats;
    }
}