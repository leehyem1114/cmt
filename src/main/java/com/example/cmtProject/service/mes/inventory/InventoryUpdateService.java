package com.example.cmtProject.service.mes.inventory;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.cmtProject.mapper.mes.inventory.InventoryUpdateMapper;
import com.example.cmtProject.mapper.mes.inventory.MaterialInventoryMapper;
import com.example.cmtProject.mapper.mes.inventory.MaterialReceiptStockMapper;
import com.example.cmtProject.mapper.mes.inventory.ProductsInventoryMapper;
import com.example.cmtProject.mapper.mes.inventory.ProductsMasterMapper;
import com.example.cmtProject.mapper.mes.inventory.ProductsProductionReceiptStockMapper;
import com.example.cmtProject.mapper.mes.inventory.ProductsProductionReceiptMapper;
import com.example.cmtProject.util.SecurityUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class InventoryUpdateService {
    
    @Autowired
    private InventoryUpdateMapper Ium;
    
    @Autowired
    private MaterialReceiptStockMapper mrsmapper;
    
    @Autowired
    private ProductsProductionReceiptStockMapper pprsmapper;
    
    @Autowired
    private MaterialInventoryMapper materialInventoryMapper;
    
    @Autowired
    private ProductsInventoryMapper productsInventoryMapper;
    
    @Autowired
    private ProductsMasterMapper ProductsMasterMapper;

    @Autowired
    private ProductsProductionReceiptMapper productsProductionReceiptMapper;
    
    @Autowired
    private ProductsInventoryService productsInventoryService;
    
    /**
     * 생산완료 처리 - LOT 상태가 CP로 변경될 때 호출
     */
    @Transactional
    public void completeProduction(Map<String, Object> lotInfo) {
        String parentCode = (String) lotInfo.get("parentPdtCode");
        String bomQty = (String) lotInfo.get("bomQty");
        String childLotCode = (String) lotInfo.get("childLotCode");
        String woCode = (String) lotInfo.get("woCode");  // woCode 받기
        String userId = SecurityUtil.getUserId();
        
        log.info("생산완료 처리 시작 - LOT: {}, 부모코드: {}, 수량: {}, 작업지시: {}", 
            childLotCode, parentCode, bomQty, woCode);
        
        // 부모 항목의 재고 차감 (FIFO)
        if (parentCode.startsWith("MTL")) {
            deductMaterialFIFO(parentCode, Long.parseLong(bomQty), userId, childLotCode, woCode);
        } else {
            // ProductsInventoryService로 제품 재고 차감 처리 위임
            Map<String, Object> params = new HashMap<>(lotInfo);
            params.put("userId", userId);
            
            Map<String, Object> result = productsInventoryService.consumeProductByProduction(params);
            
            if (!(Boolean) result.get("success")) {
                log.error("제품 재고 차감 실패: {}", result.get("message"));
                throw new RuntimeException("제품 재고 차감 실패: " + result.get("message"));
            }
        }
    }
    
    /**
     * 원자재 FIFO 차감
     */
    private void deductMaterialFIFO(String mtlCode, long qty, String userId, String lotNo, String woCode) {
        long remainingToDeduct = qty;
        long totalDeducted = 0;
        
        log.info("원자재 FIFO 차감 시작 - 자재코드: {}, 수량: {}", mtlCode, qty);
        
        // 입고별 재고 목록 조회 (FIFO 순서)
        List<Map<String, Object>> receiptStocks = mrsmapper.getStocksByMtlCodeOrderByDate(mtlCode);
        
        if (receiptStocks == null || receiptStocks.isEmpty()) {
            log.warn("자재 {}의 입고별 재고가 없으므로 일반 재고에서 차감합니다.", mtlCode);
            
            // 전체 재고에서 바로 차감
            Map<String, Object> inventoryParams = new HashMap<>();
            inventoryParams.put("mtlCode", mtlCode);
            inventoryParams.put("consumptionQty", String.valueOf(qty));
            inventoryParams.put("updatedBy", userId);
            
            materialInventoryMapper.deductInventory(inventoryParams);
            
            // 계획재고도 차감
            deductAllocatedMaterialInventory(mtlCode, qty, userId);
            return;
        }
        
        for (Map<String, Object> stock : receiptStocks) {
            if (remainingToDeduct <= 0) break;
            
            Long stockNo = Long.valueOf(stock.get("RECEIPT_STOCK_NO").toString());
            long remainingQty = Long.parseLong((String) stock.get("REMAINING_QTY"));
            
            // 차감할 수량 결정
            long qtyToDeduct = Math.min(remainingQty, remainingToDeduct);
            
            // 입고별 재고 차감
            Map<String, Object> deductParams = new HashMap<>();
            deductParams.put("receiptStockNo", stockNo);
            deductParams.put("deductQty", String.valueOf(qtyToDeduct));
            deductParams.put("updatedBy", userId);
            
            mrsmapper.deductStock(deductParams);
            
            // FIFO 이력 저장
            Map<String, Object> historyParams = new HashMap<>();
            historyParams.put("receiptStockNo", stockNo);
            historyParams.put("mtlCode", mtlCode);
            historyParams.put("consumedQty", String.valueOf(qtyToDeduct));
            historyParams.put("lotNo", lotNo);
            historyParams.put("woCode", woCode);
            historyParams.put("consumedBy", userId);
            historyParams.put("consumedDate", LocalDate.now().toString());
            
            mrsmapper.insertFIFOHistory(historyParams);
            
            totalDeducted += qtyToDeduct;
            remainingToDeduct -= qtyToDeduct;
            
            log.debug("입고분 {} 차감: {} (총 차감: {})", 
                stockNo, qtyToDeduct, totalDeducted);
        }
        
        // 전체 재고 차감
        Map<String, Object> inventoryParams = new HashMap<>();
        inventoryParams.put("mtlCode", mtlCode);
        inventoryParams.put("consumptionQty", String.valueOf(qty));
        inventoryParams.put("updatedBy", userId);
        
        materialInventoryMapper.deductInventory(inventoryParams);
        
        // 계획재고도 차감
        deductAllocatedMaterialInventory(mtlCode, qty, userId);
        
        if (remainingToDeduct > 0) {
            log.error("자재 {} 차감 실패: {}만큼 부족", mtlCode, remainingToDeduct);
            throw new RuntimeException("자재 " + mtlCode + " 재고가 " + remainingToDeduct + "만큼 부족합니다.");
        }
    }
    
    /**
     * 원자재 계획재고 차감
     */
    private void deductAllocatedMaterialInventory(String mtlCode, long qty, String userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("mtlCode", mtlCode);
        params.put("allocatedQty", String.valueOf(-qty)); // 음수로 차감
        params.put("updatedBy", userId);
        
        Ium.updateMaterialAllocatedQty(params);
    }
    
    /**
     * 제품 계획재고 차감 - @deprecated 사용하지 않음
     * ProductsInventoryService.consumeProductByProduction 메서드로 대체됨
     */
    @Deprecated
    private void deductProductFIFO(String pdtCode, long qty, String userId) {
        // ProductsInventoryService로 위임
        Map<String, Object> params = new HashMap<>();
        params.put("pdtCode", pdtCode);
        params.put("consumptionQty", String.valueOf(qty));
        params.put("updatedBy", userId);
        
        productsInventoryService.consumeProductFIFO(params);
    }
    
    /**
     * 제품 계획재고 차감 
     */
    private void deductAllocatedProductInventory(String pdtCode, long qty, String userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("pdtCode", pdtCode);
        params.put("allocatedQty", String.valueOf(-qty)); // 음수로 차감
        params.put("updatedBy", userId);
        
        Ium.updateProductAllocatedQtyDeduct(params);
    }
    
    /**
     * 생산계획 대비 자재/제품 할당 수량 업데이트 (FIFO 적용)
     */
    @Transactional
    public void updateAllocatedQuantities(Map<String, Object> params) {
        String soCode = (String) params.get("soCode");
        Long soQty = (Long) params.get("soQty");
        
        // 현재 사용자 ID 가져오기
        String userId = SecurityUtil.getUserId();
        params.put("updatedBy", userId);
        
        log.info("FIFO 기반 재고 할당 시작 - 주문코드: {}, 주문수량: {}", soCode, soQty);
        
        // BOM 항목 조회
        List<Map<String, Object>> bomItems = Ium.getBomItems(params);
        log.info("BOM 항목 조회: 총 {}개", bomItems.size());
        
        // 1. 자재 재고 업데이트 (FIFO 방식)
        allocateMaterialsFIFO(bomItems, soQty, userId);
        
        // 2. 제품 재고 업데이트 (기존 방식)
        int productUpdated = Ium.updateProductAllocatedQty(params);
        log.info("제품 재고 업데이트: {} 건", productUpdated);
    }
    
    /**
     * FIFO 방식으로 자재 재고 할당
     */
    private void allocateMaterialsFIFO(List<Map<String, Object>> bomItems, Long soQty, String userId) {
        for (Map<String, Object> item : bomItems) {
            String itemType = (String) item.get("ITEM_TYPE");
            if ("MATERIAL".equals(itemType)) {
                String mtlCode = (String) item.get("PARENT_PDT_CODE");
                long bomQty = Long.parseLong(item.get("BOM_QTY").toString());
                long requiredQty = bomQty * soQty;
                
                log.info("자재 {} FIFO 할당 진행 - 필요수량: {}", mtlCode, requiredQty);
                
                // FIFO 방식으로 재고 할당
                allocateMaterialFIFO(mtlCode, requiredQty, userId);
            }
        }
    }
    
    /**
     * 단일 자재에 대한 FIFO 할당
     */
    private void allocateMaterialFIFO(String mtlCode, long requiredQty, String userId) {
        long remainingToAllocate = requiredQty;
        long totalAllocated = 0;
        
        // 자재의 입고별 재고 목록 조회 (FIFO 순서)
        List<Map<String, Object>> receiptStocks = mrsmapper.getStocksByMtlCodeOrderByDate(mtlCode);
        
        if (receiptStocks == null || receiptStocks.isEmpty()) {
            log.info("자재 {}에 입고 이력이 없으므로 일반 재고에서 할당합니다.", mtlCode);
            
            // 전체 재고의 할당 수량만 업데이트
            Map<String, Object> updateParams = new HashMap<>();
            updateParams.put("mtlCode", mtlCode);
            updateParams.put("allocatedQty", String.valueOf(requiredQty));
            updateParams.put("updatedBy", userId);
            
            Ium.updateMaterialAllocatedQty(updateParams);
            return;
        }
        
        for (Map<String, Object> stock : receiptStocks) {
            if (remainingToAllocate <= 0) break;
            
            Long stockNo = Long.valueOf(stock.get("RECEIPT_STOCK_NO").toString());
            long remainingQty = Long.parseLong((String) stock.get("REMAINING_QTY"));
            
            // 할당할 수량 결정
            long qtyToAllocate = Math.min(remainingQty, remainingToAllocate);
            
            // 입고별 재고 차감
            Map<String, Object> deductParams = new HashMap<>();
            deductParams.put("receiptStockNo", stockNo);
            deductParams.put("deductQty", String.valueOf(qtyToAllocate));
            deductParams.put("updatedBy", userId);
            
            mrsmapper.deductStock(deductParams);
            
            // 할당량 누적 및 남은 할당량 업데이트
            totalAllocated += qtyToAllocate;
            remainingToAllocate -= qtyToAllocate;
            
            log.debug("입고분 {} 할당: {} (총 할당: {})", 
                stockNo, qtyToAllocate, totalAllocated);
        }
        
        // 전체 재고의 할당 수량 업데이트
        Map<String, Object> updateParams = new HashMap<>();
        updateParams.put("mtlCode", mtlCode);
        updateParams.put("allocatedQty", String.valueOf(totalAllocated));
        updateParams.put("updatedBy", userId);
        
        Ium.updateMaterialAllocatedQty(updateParams);
        
        if (remainingToAllocate > 0) {
            log.warn("자재 {} 할당 부족: {}만큼 부족", mtlCode, remainingToAllocate);
        }
    }
    
    /**
     * 생산완료된 제품/반제품 입고 처리
     */
    @Transactional
    public void receiveProductionItem(Map<String, Object> params) {
        String pdtCode = (String) params.get("pdtCode");
        String woQty = (String) params.get("woQty");
        String childLotCode = (String) params.get("childLotCode");
        String userId = SecurityUtil.getUserId();
        
        log.info("생산완료품 입고 처리 - 제품코드: {}, LOT: {}, 수량: {}", pdtCode, childLotCode, woQty);
        
        // 1. 제품 기준정보 조회
        Map<String, Object> productParams = new HashMap<>();
        productParams.put("PDT_CODE", pdtCode);
        Map<String, Object> productInfo = ProductsMasterMapper.selectSingleProducts(productParams);
        
        // 2. 재고 증가
        Map<String, Object> inventoryParams = new HashMap<>();
        inventoryParams.put("pdtCode", pdtCode);
        inventoryParams.put("receivedQty", woQty);
        
        // 창고/위치 정보 설정 - 제품 기준정보의 값만 사용
        String warehouseCode = (String) productInfo.get("DEFAULT_WAREHOUSE_CODE");
        String locationCode = (String) productInfo.get("DEFAULT_LOCATION_CODE");
        
        inventoryParams.put("warehouseCode", warehouseCode);
        inventoryParams.put("locationCode", locationCode);
        inventoryParams.put("updatedBy", userId);
        
        productsInventoryMapper.mergeInventory(inventoryParams);
        
        // 3. 생산 입고 정보 생성
        Map<String, Object> receiptParams = new HashMap<>();
        receiptParams.put("receiptCode", "PR-" + System.currentTimeMillis());
        receiptParams.put("productionCode", childLotCode);
        receiptParams.put("pdtCode", pdtCode);
        receiptParams.put("receivedQty", woQty);
        receiptParams.put("receiptDate", LocalDate.now().toString());
        receiptParams.put("receiptStatus", "입고완료");
        receiptParams.put("warehouseCode", warehouseCode);
        receiptParams.put("locationCode", locationCode);
        receiptParams.put("receiver", userId);
        receiptParams.put("createdBy", userId);
        
        productsProductionReceiptMapper.insertProductionReceipt(receiptParams);
        Long receiptNo = productsProductionReceiptMapper.getLastReceiptNo();
        
        // 4. FIFO 관리용 생산입고 이력 저장
        Map<String, Object> stockParams = new HashMap<>();
        stockParams.put("receiptNo", receiptNo);  // 생산입고 번호 연결
        stockParams.put("productionCode", childLotCode);  // LOT 번호를 생산코드로 사용
        stockParams.put("pdtCode", pdtCode);
        stockParams.put("remainingQty", woQty); // 남은 수량 설정 (초기엔 입고량과 동일)
        stockParams.put("productionDate", LocalDate.now().toString());
        stockParams.put("lotNo", childLotCode);
        stockParams.put("createdBy", userId);
        
        pprsmapper.insertStock(stockParams);
        
        log.info("생산완료품 입고 완료 - 제품: {}, 수량: {}", pdtCode, woQty);
    }
}