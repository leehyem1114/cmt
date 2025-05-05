package com.example.cmtProject.service.mes.inventory;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.cmtProject.mapper.mes.inventory.InventoryUpdateMapper;
import com.example.cmtProject.mapper.mes.inventory.MaterialReceiptStockMapper;
import com.example.cmtProject.mapper.mes.inventory.MaterialInventoryMapper;
import com.example.cmtProject.mapper.mes.inventory.ProductsInventoryMapper;
import com.example.cmtProject.mapper.mes.inventory.ProductsIssueStockMapper;
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
    private MaterialInventoryMapper materialInventoryMapper;
    
    @Autowired
    private ProductsInventoryMapper productsInventoryMapper;
    
    @Autowired
    private ProductsIssueStockMapper productsIssueStockMapper;
    
    /**
     * 생산완료 처리 - LOT 상태가 CP로 변경될 때 호출
     */
    @Transactional
    public void completeProduction(Map<String, Object> lotInfo) {
        String parentCode = (String) lotInfo.get("parentPdtCode");
        String bomQty = (String) lotInfo.get("bomQty");
        String userId = SecurityUtil.getUserId();
        
        log.info("생산완료 처리 시작 - LOT: {}, 부모코드: {}, 수량: {}", 
            lotInfo.get("childLotCode"), parentCode, bomQty);
        
        // 부모 항목의 재고 차감 (FIFO)
        if (parentCode.startsWith("MTL")) {
            deductMaterialFIFO(parentCode, Double.parseDouble(bomQty), userId);
        } else {
            deductProductFIFO(parentCode, Double.parseDouble(bomQty), userId);
        }
        
        // 생산된 자식 제품의 재고 추가( 검수 완료정보를 받아서 추가해야함)
       // addProductToInventory(lotInfo, userId);
    }
    
    /**
     * 원자재 FIFO 차감
     */
    private void deductMaterialFIFO(String mtlCode, double qty, String userId) {
        double remainingToDeduct = qty;
        double totalDeducted = 0;
        
        log.info("원자재 FIFO 차감 시작 - 자재코드: {}, 수량: {}", mtlCode, qty);
        
        // 입고별 재고 목록 조회 (FIFO 순서)
        List<Map<String, Object>> receiptStocks = mrsmapper.getStocksByMtlCodeOrderByDate(mtlCode);
        
        if (receiptStocks == null || receiptStocks.isEmpty()) {
            log.warn("자재 {}의 입고별 재고가 없습니다.", mtlCode);
            throw new RuntimeException("자재 " + mtlCode + "의 입고별 재고가 없습니다.");
        }
        
        for (Map<String, Object> stock : receiptStocks) {
            if (remainingToDeduct <= 0) break;
            
            Long stockNo = Long.valueOf(stock.get("RECEIPT_STOCK_NO").toString());
            double remainingQty = Double.parseDouble((String) stock.get("REMAINING_QTY"));
            
            // 차감할 수량 결정
            double qtyToDeduct = Math.min(remainingQty, remainingToDeduct);
            
            // 입고별 재고 차감
            Map<String, Object> deductParams = new HashMap<>();
            deductParams.put("receiptStockNo", stockNo);
            deductParams.put("deductQty", String.valueOf(qtyToDeduct));
            deductParams.put("updatedBy", userId);
            
            mrsmapper.deductStock(deductParams);
            
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
        
        // 계획재고도 차감 (필요한 경우)
        deductAllocatedMaterialInventory(mtlCode, qty, userId);
        
        if (remainingToDeduct > 0) {
            log.error("자재 {} 차감 실패: {}만큼 부족", mtlCode, remainingToDeduct);
            throw new RuntimeException("자재 " + mtlCode + " 재고가 " + remainingToDeduct + "만큼 부족합니다.");
        }
    }
    
    /**
     * 반제품 FIFO 차감
     */
    private void deductProductFIFO(String pdtCode, double qty, String userId) {
        double remainingToDeduct = qty;
        double totalDeducted = 0;
        
        log.info("반제품 FIFO 차감 시작 - 제품코드: {}, 수량: {}", pdtCode, qty);
        
        // 출고별 재고 목록 조회 (FIFO 순서) - 실제로는 생산입고 이력
        List<Map<String, Object>> issueStocks = productsIssueStockMapper.getStocksForFIFO(pdtCode);
        
        if (issueStocks == null || issueStocks.isEmpty()) {
            log.warn("제품 {}의 출고별 재고가 없습니다.", pdtCode);
            throw new RuntimeException("제품 " + pdtCode + "의 출고별 재고가 없습니다.");
        }
        
        for (Map<String, Object> stock : issueStocks) {
            if (remainingToDeduct <= 0) break;
            
            Long stockNo = Long.valueOf(stock.get("ISSUE_STOCK_NO").toString());
            double availableQty = Double.parseDouble((String) stock.get("ISSUED_QTY"));
            
            // 차감할 수량 결정
            double qtyToDeduct = Math.min(availableQty, remainingToDeduct);
            
            // 출고별 재고 차감 
            Map<String, Object> deductParams = new HashMap<>();
            deductParams.put("issueStockNo", stockNo);
            deductParams.put("deductQty", String.valueOf(qtyToDeduct));
            deductParams.put("updatedBy", userId);
            
            productsIssueStockMapper.deductStock(deductParams);
            
            totalDeducted += qtyToDeduct;
            remainingToDeduct -= qtyToDeduct;
            
            log.debug("출고분 {} 차감: {} (총 차감: {})", 
                stockNo, qtyToDeduct, totalDeducted);
        }
        
        // 전체 재고 차감
        Map<String, Object> inventoryParams = new HashMap<>();
        inventoryParams.put("pdtCode", pdtCode);
        inventoryParams.put("consumptionQty", String.valueOf(qty));
        inventoryParams.put("updatedBy", userId);
        
        productsInventoryMapper.deductInventory(inventoryParams);
        
        // 계획재고도 차감 (필요한 경우)
        deductAllocatedProductInventory(pdtCode, qty, userId);
        
        if (remainingToDeduct > 0) {
            log.error("제품 {} 차감 실패: {}만큼 부족", pdtCode, remainingToDeduct);
            throw new RuntimeException("제품 " + pdtCode + " 재고가 " + remainingToDeduct + "만큼 부족합니다.");
        }
    }
    
    /**
     * 생산된 자식 제품 재고 추가   ==> 공정검사 완료후 추가가되어야함
     */
    private void addProductToInventory(Map<String, Object> lotInfo, String userId) {
        String childCode = (String) lotInfo.get("childPdtCode");
        String childLotCode = (String) lotInfo.get("childLotCode");
        String woQty = (String) lotInfo.get("bomQty");
        
        log.info("생산완료 제품 재고 추가 - 제품코드: {}, LOT: {}, 수량: {}", childCode, childLotCode, woQty);
        
        // 재고 증가
        Map<String, Object> inventoryParams = new HashMap<>();
        inventoryParams.put("pdtCode", childCode);
        inventoryParams.put("receivedQty", woQty);
        // 창고/위치 정보가 없다면 기본값 설정
        inventoryParams.put("warehouseCode", "W001"); // 기본 창고 코드
        inventoryParams.put("locationCode", "L001"); // 기본 위치 코드
        inventoryParams.put("updatedBy", userId);
        
        productsInventoryMapper.mergeInventory(inventoryParams);
        
        // 생산입고 재고정보 추가 (FIFO 관리용)
        Map<String, Object> stockParams = new HashMap<>();
        stockParams.put("issueNo", getNextIssueNo());
        stockParams.put("pdtCode", childCode);
        stockParams.put("issuedQty", woQty);
        stockParams.put("issueDate", new java.util.Date().toString());
        stockParams.put("lotNo", childLotCode);
        stockParams.put("createdBy", userId);
        
        productsIssueStockMapper.insertStock(stockParams);
    }
    
    /**
     * 다음 출고 번호 생성 (임시)
     */
    private Long getNextIssueNo() {
        // TODO: 실제 구현 시 ProductsIssueMapper에서 nextIssueNo 메서드 추가
        return System.currentTimeMillis();
    }
    
    /**
     * 원자재 계획재고 차감
     */
    private void deductAllocatedMaterialInventory(String mtlCode, double qty, String userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("mtlCode", mtlCode);
        params.put("allocatedQty", String.valueOf(-qty)); // 음수로 차감
        params.put("updatedBy", userId);
        
        Ium.updateMaterialAllocatedQty(params);
    }
    
    /**
     * 제품 계획재고 차감 
     */
    private void deductAllocatedProductInventory(String pdtCode, double qty, String userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("pdtCode", pdtCode);
        params.put("allocatedQty", String.valueOf(-qty)); // 음수로 차감
        params.put("updatedBy", userId);
        
        // TODO: InventoryUpdateMapper에 제품 계획재고 차감 메서드 추가 필요
        // Ium.updateProductAllocatedQtyDeduct(params);
    }
    
    /**
     * 생산계획 대비 자재/제품 할당 수량 업데이트 (FIFO 적용)
     * @param params soCode - 판매주문 코드, soQty - 주문 수량, updatedBy - 수정자
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
        
        // BOM 항목 로깅
        for (Map<String, Object> item : bomItems) {
            log.info("아이템: {} (타입: {}, 필요수량: {} * {} = {})", 
                item.get("PARENT_PDT_CODE"), 
                item.get("ITEM_TYPE"),
                item.get("BOM_QTY"), 
                soQty,
                Double.parseDouble(item.get("BOM_QTY").toString()) * soQty);
        }
        
        // 1. 자재 재고 업데이트 (FIFO 방식)
        allocateMaterialsFIFO(bomItems, soQty, userId);
        
        // 2. 제품 재고 업데이트 (기존 방식)
        int productUpdated = Ium.updateProductAllocatedQty(params);
        log.info("제품 재고 업데이트: {} 건", productUpdated);
    }
    
    /**
     * FIFO 방식으로 자재 재고 할당
     * @param bomItems BOM 항목 목록
     * @param soQty 주문 수량
     * @param userId 사용자 ID
     */
    private void allocateMaterialsFIFO(List<Map<String, Object>> bomItems, Long soQty, String userId) {
        for (Map<String, Object> item : bomItems) {
            String itemType = (String) item.get("ITEM_TYPE");
            if ("MATERIAL".equals(itemType)) {
                String mtlCode = (String) item.get("PARENT_PDT_CODE");
                double bomQty = Double.parseDouble(item.get("BOM_QTY").toString());
                double requiredQty = bomQty * soQty;
                
                log.info("자재 {} FIFO 할당 진행 - 필요수량: {}", mtlCode, requiredQty);
                
                // FIFO 방식으로 재고 할당
                allocateMaterialFIFO(mtlCode, requiredQty, userId);
            }
        }
    }
    
    /**
     * 단일 자재에 대한 FIFO 할당
     * @param mtlCode 자재 코드
     * @param requiredQty 필요 수량
     * @param userId 사용자 ID
     */
    private void allocateMaterialFIFO(String mtlCode, double requiredQty, String userId) {
        double remainingToAllocate = requiredQty;
        double totalAllocated = 0;
        
        // 자재의 총 가용 재고 확인
        Double totalAvailable = mrsmapper.getTotalAvailableStock(mtlCode);
        if (totalAvailable == null || totalAvailable < requiredQty) {
            log.warn("자재 {} 가용 재고 부족: 필요={}, 가용={}", 
                mtlCode, requiredQty, totalAvailable != null ? totalAvailable : 0);
        }
        
        // 자재의 입고별 재고 목록 조회 (FIFO 순서)
        List<Map<String, Object>> receiptStocks = mrsmapper.getStocksByMtlCodeOrderByDate(mtlCode);
        
        if (receiptStocks == null || receiptStocks.isEmpty()) {
            log.warn("자재 {}의 입고별 재고가 없습니다.", mtlCode);
            return;
        }
        
        for (Map<String, Object> stock : receiptStocks) {
            if (remainingToAllocate <= 0) break;
            
            Long stockNo = Long.valueOf(stock.get("RECEIPT_STOCK_NO").toString());
            double remainingQty = Double.parseDouble((String) stock.get("REMAINING_QTY"));
            
            // 할당할 수량 결정
            double qtyToAllocate = Math.min(remainingQty, remainingToAllocate);
            
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
}