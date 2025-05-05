package com.example.cmtProject.mapper.mes.inventory;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface MaterialReceiptStockMapper {
    
    /**
     * 입고별 재고 정보 저장
     */
    public int insertStock(Map<String, Object> params);
    
    /**
     * 자재코드별 입고 재고 목록 조회 (입고일 순)
     */
    public List<Map<String, Object>> getStocksByMtlCodeOrderByDate(@Param("mtlCode") String mtlCode);
    
    /**
     * 입고별 재고 차감
     */
    public int deductStock(Map<String, Object> params);
    
    /**
     * 자재의 총 가용 재고 조회
     */
    public Long getTotalAvailableStock(@Param("mtlCode") String mtlCode);
    
    /**
     * FIFO 이력 저장
     */
    public int insertFIFOHistory(Map<String, Object> params);
    
    /**
     * FIFO 이력 조회
     */
    public List<Map<String, Object>> getFIFOHistory(@Param("mtlCode") String mtlCode);
}