package com.example.cmtProject.service.mes.inventory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.example.cmtProject.mapper.mes.inventory.MaterialInventoryMapper;
import com.example.cmtProject.mapper.mes.inventory.MaterialMasterMapper;
import com.example.cmtProject.mapper.mes.inventory.MaterialReceiptMapper;
import com.example.cmtProject.mapper.mes.inventory.MaterialReceiptStockMapper;
import com.example.cmtProject.mapper.mes.inventory.MaterialReceiptHistoryMapper;
import com.example.cmtProject.util.SecurityUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MaterialInventoryService {
    
    @Autowired
    private MaterialInventoryMapper mImapper;
    
    @Autowired
    private MaterialReceiptStockMapper mRsmapper;

    @Autowired
    private MaterialMasterMapper mMmapper;
    
    @Autowired
    private MaterialReceiptMapper mRmapper;
    
    @Autowired
    private MaterialReceiptHistoryMapper mRhmapper;
    
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
            List<Map<String, Object>> stockList = mRsmapper.getStocksByMtlCodeOrderByDate(mtlCode);
            
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
                
                mRsmapper.deductStock(deductParams);
                
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
                    
                    mRsmapper.insertFIFOHistory(historyParams);
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
        List<Map<String, Object>> stockList = mRsmapper.getStocksByMtlCodeOrderByDate(mtlCode);
        
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
        
        List<Map<String, Object>> historyList = mRsmapper.getFIFOHistory(mtlCode);
        
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
        
        List<Map<String, Object>> stockList = mRsmapper.getStocksByMtlCodeOrderByDate(mtlCode);
        
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
    
    /**
     * 기본 재고 정보 자동 생성
     * 아직 재고 정보가 없는 원자재에 대해서만 기본 재고 정보 생성
     */
    @Transactional
    public Map<String, Object> generateInitialInventoryData() {
        Map<String, Object> resultMap = new HashMap<>();
        int createdCount = 0;
        List<String> failedItems = new ArrayList<>();
        
        try {
            log.info("미등록 원자재에 대한 기본 재고 데이터 생성 시작");
            
            // 모든 원자재 조회 (사용 중인 것만)
            Map<String, Object> params = new HashMap<>();
            List<Map<String, Object>> materialList = mMmapper.selectMaterials(params);
            
            // 현재 로그인한 사용자 정보
            String userId = SecurityUtil.getUserId();
            
            for (Map<String, Object> material : materialList) {
                try {
                    String mtlCode = (String) material.get("MTL_CODE");
                    
                    // 이미 재고 정보가 존재하는지 확인
                    Map<String, Object> existingInventory = mImapper.getInventoryByMtlCode(mtlCode);
                    
                    if (existingInventory == null) {
                        Map<String, Object> inventoryData = new HashMap<>();
                        inventoryData.put("MTL_CODE", mtlCode);
                        inventoryData.put("WAREHOUSE_CODE", material.get("DEFAULT_WAREHOUSE_CODE"));
                        inventoryData.put("LOCATION_CODE", material.get("DEFAULT_LOCATION_CODE"));
                        inventoryData.put("CURRENT_QTY", "0");
                        inventoryData.put("ALLOCATED_QTY", "0");
                        // AVAILABLE_QTY는 트리거에 의해 자동 계산됨
                        inventoryData.put("LAST_MOVEMENT_DATE", new Date());
                        inventoryData.put("SAFETY_STOCK_ALERT", "N");
                        inventoryData.put("CREATED_BY", userId);
                        
                        int result = mImapper.insertInventory(inventoryData);
                        
                        if (result > 0) {
                            createdCount++;
                            log.info("원자재 {}에 대한 기본 재고 정보 생성 성공", mtlCode);
                        } else {
                            failedItems.add(mtlCode);
                            log.warn("원자재 {}에 대한 기본 재고 정보 생성 실패", mtlCode);
                        }
                    } else {
                        log.info("원자재 {}는 이미 재고 정보가 존재합니다.", mtlCode);
                    }
                } catch (Exception e) {
                    String mtlCode = (String) material.get("MTL_CODE");
                    failedItems.add(mtlCode);
                    log.error("원자재 {} 재고 정보 생성 중 오류: {}", mtlCode, e.getMessage());
                }
            }
            
            resultMap.put("success", true);
            resultMap.put("message", createdCount + "개의 원자재에 대한 기본 재고 정보가 생성되었습니다.");
            resultMap.put("createdCount", createdCount);
            resultMap.put("failedItems", failedItems);
            
            log.info("원자재 기본 재고 데이터 생성 완료: {}개 생성, {}개 실패", 
                    createdCount, failedItems.size());
            
        } catch (Exception e) {
            log.error("재고 데이터 생성 중 오류 발생: {}", e.getMessage(), e);
            resultMap.put("success", false);
            resultMap.put("message", "재고 데이터 생성 중 오류 발생: " + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        
        return resultMap;
    }

    /**
     * 개별 원자재에 대한 기본 재고 데이터 생성
     * 기존 재고 정보가 없는 경우에만 생성
     */
    @Transactional
    public Map<String, Object> generateMaterialInventory(String mtlCode) {
        Map<String, Object> resultMap = new HashMap<>();
        
        try {
            log.info("원자재 {}에 대한 기본 재고 데이터 생성 시작", mtlCode);
            
            // 원자재 정보 조회
            Map<String, Object> param = new HashMap<>();
            param.put("MTL_CODE", mtlCode);
            Map<String, Object> material = mMmapper.selectSingleMaterials(param);
            
            if (material == null) {
                resultMap.put("success", false);
                resultMap.put("message", "원자재 정보를 찾을 수 없습니다: " + mtlCode);
                return resultMap;
            }
            
            // 이미 재고 정보가 존재하는지 확인
            Map<String, Object> existingInventory = mImapper.getInventoryByMtlCode(mtlCode);
            
            if (existingInventory != null) {
                resultMap.put("success", false);
                resultMap.put("message", "이미 재고 정보가 존재합니다: " + mtlCode);
                return resultMap;
            }
            
            // 현재 로그인한 사용자 정보
            String userId = SecurityUtil.getUserId();
            
            // 재고 정보 생성
            Map<String, Object> inventoryData = new HashMap<>();
            inventoryData.put("MTL_CODE", mtlCode);
            inventoryData.put("WAREHOUSE_CODE", material.get("DEFAULT_WAREHOUSE_CODE"));
            inventoryData.put("LOCATION_CODE", material.get("DEFAULT_LOCATION_CODE"));
            inventoryData.put("CURRENT_QTY", "0");
            inventoryData.put("ALLOCATED_QTY", "0");
            // AVAILABLE_QTY는 트리거에 의해 자동 계산됨
            inventoryData.put("LAST_MOVEMENT_DATE", new Date());
            inventoryData.put("SAFETY_STOCK_ALERT", "N");
            inventoryData.put("CREATED_BY", userId);
            
            int result = mImapper.insertInventory(inventoryData);
            
            if (result > 0) {
                resultMap.put("success", true);
                resultMap.put("message", "재고 정보가 생성되었습니다: " + mtlCode);
                log.info("원자재 {}에 대한 기본 재고 정보 생성 성공", mtlCode);
            } else {
                resultMap.put("success", false);
                resultMap.put("message", "재고 정보 생성에 실패했습니다: " + mtlCode);
                log.warn("원자재 {}에 대한 기본 재고 정보 생성 실패", mtlCode);
            }
            
        } catch (Exception e) {
            log.error("재고 데이터 생성 중 오류 발생: {}", e.getMessage(), e);
            resultMap.put("success", false);
            resultMap.put("message", "재고 데이터 생성 중 오류 발생: " + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        
        return resultMap;
    }
    
    /**
     * 임시 발주입고 처리 (초기 재고 데이터 생성용)
     * FIFO 관리를 위한 발주입고 이력 생성 및 재고 증가 처리
     */
    @Transactional
    public Map<String, Object> createTempMaterialReceipt(Map<String, Object> params) {
        Map<String, Object> resultMap = new HashMap<>();
        
        try {
            log.info("임시 발주입고 처리 시작: {}", params);
            
            // 필수 파라미터 검증
            if (!params.containsKey("mtlCode") || !params.containsKey("qty")) {
                resultMap.put("success", false);
                resultMap.put("message", "필수 파라미터가 누락되었습니다. (mtlCode, qty)");
                return resultMap;
            }
            
            String mtlCode = (String) params.get("mtlCode");
            String qty = (String) params.get("qty");
            String lotNo = (String) params.getOrDefault("lotNo", "INIT-" + mtlCode + "-" + System.currentTimeMillis());
            
            // 원자재 정보 조회
            Map<String, Object> param = new HashMap<>();
            param.put("MTL_CODE", mtlCode);
            Map<String, Object> materialInfo = mMmapper.selectSingleMaterials(param);
            
            if (materialInfo == null) {
                resultMap.put("success", false);
                resultMap.put("message", "원자재 정보를 찾을 수 없습니다: " + mtlCode);
                return resultMap;
            }
            
            // 현재 사용자 ID 가져오기
            String userId = SecurityUtil.getUserId();
            
            // 1. 발주입고 정보 생성
            String receiptCode = "RC-INIT-" + mtlCode + "-" + System.currentTimeMillis();
            
            Map<String, Object> receiptParams = new HashMap<>();
            receiptParams.put("receiptCode", receiptCode);
            receiptParams.put("poCode", "PO-INIT-" + mtlCode);  // 가상의 발주 코드
            receiptParams.put("mtlCode", mtlCode);
            receiptParams.put("receivedQty", qty);
            receiptParams.put("receiptDate", java.time.LocalDate.now().toString());
            receiptParams.put("receiptStatus", "입고완료");
            receiptParams.put("warehouseCode", materialInfo.get("DEFAULT_WAREHOUSE_CODE"));
            receiptParams.put("locationCode", materialInfo.get("DEFAULT_LOCATION_CODE"));
            receiptParams.put("lotNo", lotNo);
            receiptParams.put("receiver", userId);
            receiptParams.put("createdBy", userId);
            
            // MaterialReceiptMapper를 통한 데이터 저장
            mRmapper.insertMaterialReceipt(receiptParams);
            Long receiptNo = mRmapper.getLastReceiptNo();
            
            // 2. FIFO 관리용 입고 이력 저장
            Map<String, Object> stockParams = new HashMap<>();
            stockParams.put("receiptNo", receiptNo);
            stockParams.put("mtlCode", mtlCode);
            stockParams.put("remainingQty", qty);
            stockParams.put("receiptDate", java.time.LocalDate.now().toString());
            stockParams.put("createdBy", userId);
            
            mRsmapper.insertStock(stockParams);
            
            // 3. 원자재 재고 증가
            Map<String, Object> inventoryParams = new HashMap<>();
            inventoryParams.put("mtlCode", mtlCode);
            inventoryParams.put("receivedQty", qty);
            inventoryParams.put("warehouseCode", materialInfo.get("DEFAULT_WAREHOUSE_CODE"));
            inventoryParams.put("locationCode", materialInfo.get("DEFAULT_LOCATION_CODE"));
            inventoryParams.put("updatedBy", userId);
            
            mImapper.mergeInventory(inventoryParams);
            
            // 4. 입고 이력 남기기
            Map<String, Object> historyMap = new HashMap<>();
            historyMap.put("receiptNo", receiptNo);
            historyMap.put("actionType", "임시입고");
            historyMap.put("actionDescription", "초기 재고 데이터 생성을 위한 임시 입고 처리");
            historyMap.put("actionUser", userId);
            historyMap.put("createdBy", userId);
            
            mRhmapper.insertHistory(historyMap);
            
            resultMap.put("success", true);
            resultMap.put("message", "임시 발주입고 처리가 완료되었습니다.");
            resultMap.put("receiptNo", receiptNo);
            resultMap.put("mtlCode", mtlCode);
            resultMap.put("qty", qty);
            resultMap.put("lotNo", lotNo);
            
            log.info("임시 발주입고 처리 완료: 원자재코드={}, 수량={}, LOT={}", mtlCode, qty, lotNo);
            
        } catch (Exception e) {
            log.error("임시 발주입고 처리 중 오류 발생: {}", e.getMessage(), e);
            resultMap.put("success", false);
            resultMap.put("message", "오류가 발생했습니다: " + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        
        return resultMap;
    }

    /**
     * 모든 원자재에 대한 임시 발주입고 처리 (초기 재고 데이터 일괄 생성)
     * 
     * @param params 입력 파라미터 (기본 수량, 창고/위치 코드 등)
     * @return 처리 결과
     */
    @Transactional
    public Map<String, Object> createTempMaterialReceiptForAll(Map<String, Object> params) {
        Map<String, Object> resultMap = new HashMap<>();
        int successCount = 0;
        int failCount = 0;
        List<String> failedItems = new ArrayList<>();
        
        try {
            log.info("전체 원자재 임시 발주입고 처리 시작: {}", params);
            
            // 필수 파라미터 검증
            if (!params.containsKey("defaultQty")) {
                resultMap.put("success", false);
                resultMap.put("message", "기본 수량은 필수입니다.");
                return resultMap;
            }
            
            String defaultQty = (String) params.get("defaultQty");
            
            // 원자재 목록 조회 (사용 중인 것만)
            Map<String, Object> queryParams = new HashMap<>();
            List<Map<String, Object>> materialsList = mMmapper.selectMaterials(queryParams);
            
            if (materialsList.isEmpty()) {
                resultMap.put("success", false);
                resultMap.put("message", "처리할 원자재가 없습니다.");
                return resultMap;
            }
            
            log.info("처리할 원자재 수: {}", materialsList.size());
            
            // 현재 사용자 ID 가져오기
            String userId = SecurityUtil.getUserId();
            
            // 각 원자재에 대해 임시 발주입고 처리
            for (Map<String, Object> material : materialsList) {
                try {
                    String mtlCode = (String) material.get("MTL_CODE");
                    
                    // 이미 재고가 있는지 확인
                    Map<String, Object> inventory = mImapper.getInventoryByMtlCode(mtlCode);
                    
                    // 재고가 있고 임시 데이터 생성 옵션이 false인 경우 스킵
                    boolean overwriteExisting = params.containsKey("overwriteExisting") && 
                                              (Boolean) params.get("overwriteExisting");
                    
                    // 재고가 있고(재고 수량이 0보다 큰 경우) 임시 데이터 생성 옵션이 false인 경우 스킵
                    if (inventory != null && 
                        inventory.get("CURRENT_QTY") != null && 
                        Integer.parseInt(inventory.get("CURRENT_QTY").toString()) > 0 && 
                        !overwriteExisting) {
                        log.info("원자재 {}은(는) 이미 재고가 있어 스킵합니다.", mtlCode);
                        continue;
                    }
                    
                    // LOT 번호 생성
                    String lotNo = "INIT-" + mtlCode + "-" + System.currentTimeMillis();
                    
                    // 임시 발주입고 처리 파라미터 설정
                    Map<String, Object> entryParams = new HashMap<>();
                    entryParams.put("mtlCode", mtlCode);
                    entryParams.put("qty", defaultQty);
                    entryParams.put("lotNo", lotNo);
                    
                    // 개별 임시 발주입고 처리 호출
                    Map<String, Object> result = createTempMaterialReceipt(entryParams);
                    
                    if ((Boolean) result.get("success")) {
                        successCount++;
                        log.info("원자재 {} 임시 발주입고 성공: {}", mtlCode, result.get("message"));
                    } else {
                        failCount++;
                        failedItems.add(mtlCode);
                        log.warn("원자재 {} 임시 발주입고 실패: {}", mtlCode, result.get("message"));
                    }
                    
                    // 처리 간격을 두어 시스템 부하 감소
                    Thread.sleep(10);
                    
                } catch (Exception e) {
                    failCount++;
                    failedItems.add((String) material.get("MTL_CODE"));
                    log.error("원자재 {} 처리 중 오류: {}", material.get("MTL_CODE"), e.getMessage(), e);
                }
            }
            
            if (successCount > 0) {
                String message = successCount + "개 원자재의 임시 발주입고가 완료되었습니다.";
                if (failCount > 0) {
                    message += " (" + failCount + "개 실패)";
                }
                
                resultMap.put("success", true);
                resultMap.put("message", message);
            } else {
                resultMap.put("success", false);
                resultMap.put("message", "모든 원자재의 임시 발주입고 처리에 실패했습니다.");
            }
            
            resultMap.put("successCount", successCount);
            resultMap.put("failCount", failCount);
            resultMap.put("failedItems", failedItems);
            
            log.info("전체 원자재 임시 발주입고 처리 완료: 성공={}, 실패={}", successCount, failCount);
            
        } catch (Exception e) {
            log.error("전체 원자재 임시 발주입고 처리 중 오류: {}", e.getMessage(), e);
            resultMap.put("success", false);
            resultMap.put("message", "처리 중 오류가 발생했습니다: " + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        
        return resultMap;
    }
}