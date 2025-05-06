package com.example.cmtProject.mapper.mes.inventory;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface ProductsIssueStockMapper {
	
    /**
     * 출고별 재고 정보 저장
     * 
     * @param params 재고 정보 (issueNo, pdtCode, issuedQty, issueDate, lotNo, createdBy 포함)
     * @return 처리 건수
     */
    public int insertStock(Map<String, Object> params);
    
    /**
     * 제품코드별 출고 재고 목록 조회 (출고일 순)
     * 
     * @param pdtCode 제품 코드
     * @return 출고별 재고 목록
     */
    public List<Map<String, Object>> getStocksByPdtCodeOrderByDate(@Param("pdtCode") String pdtCode);
    
    /**
     * FIFO 방식으로 제품 재고 관리를 위한 목록 조회 (날짜 오름차순)
     * 
     * @param pdtCode 제품 코드
     * @return 재고 목록
     */
    public List<Map<String, Object>> getStocksForFIFO(@Param("pdtCode") String pdtCode);
    
    /**
     * 출고별 재고 차감
     */
    public int deductStock(Map<String, Object> params);
    
    /**
     * FIFO 이력 저장
     */
    public int insertFIFOHistory(Map<String, Object> params);
    
    /**
     * FIFO 이력 조회
     */
    public List<Map<String, Object>> getFIFOHistory(@Param("pdtCode") String pdtCode);
    
    /**
     * 다음 출고 번호 조회
     */
    public Long getNextIssueNo();
    
    /**
     * 생산 입고 번호 생성
     */
    public String generateProductionReceiptCode();

    /**
     * 제품별 생산 입고 이력 조회 (FIFO 관리용)
     */
    public List<Map<String, Object>> getProductionReceiptsForFIFO(@Param("pdtCode") String pdtCode);
}