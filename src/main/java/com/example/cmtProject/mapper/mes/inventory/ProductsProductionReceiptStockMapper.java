package com.example.cmtProject.mapper.mes.inventory;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 제품/반제품 생산입고별 재고 관리 매퍼 인터페이스
 * FIFO 방식의 재고 관리를 위한 생산입고 이력 데이터를 처리합니다.
 */
@Mapper
@Repository
public interface ProductsProductionReceiptStockMapper {
    
    /**
     * 생산입고별 재고 정보 저장
     * 
     * @param params 재고 정보 (productionCode, pdtCode, remainingQty, productionDate, lotNo, createdBy 포함)
     * @return 처리 건수
     */
    public int insertStock(Map<String, Object> params);
    
    /**
     * 제품코드별 생산입고 재고 목록 조회 (생산일자 순정렬)
     * 
     * @param pdtCode 제품/반제품 코드
     * @return 생산입고별 재고 목록 (FIFO 순서)
     */
    public List<Map<String, Object>> getStocksByPdtCodeOrderByDate(@Param("pdtCode") String pdtCode);
    
    /**
     * FIFO 방식으로 제품 재고 관리를 위한 목록 조회
     * 
     * @param pdtCode 제품/반제품 코드
     * @return 재고 목록 (남은 수량이 있는 것만, 생산일자 오름차순)
     */
    public List<Map<String, Object>> getStocksForFIFO(@Param("pdtCode") String pdtCode);
    
    /**
     * 생산입고별 재고 차감
     * 
     * @param params 차감 정보 (productionReceiptStockNo, deductQty, updatedBy 포함)
     * @return 처리 건수
     */
    public int deductStock(Map<String, Object> params);
    
    /**
     * 제품의 총 가용 재고 조회
     * 
     * @param pdtCode 제품/반제품 코드
     * @return 총 가용 재고
     */
    public Double getTotalAvailableStock(@Param("pdtCode") String pdtCode);
}