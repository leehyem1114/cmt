package com.example.cmtProject.mapper.mes.inventory;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface ProductsProductionReceiptMapper {
    
    /**
     * 생산 입고 정보 저장
     * 
     * @param params 입고 정보
     * @return 처리 건수
     */
    public int insertProductionReceipt(Map<String, Object> params);
    
    /**
     * 마지막 저장된 생산 입고 번호 조회
     * 
     * @return 입고 번호
     */
    public Long getLastReceiptNo();
    
    /**
     * 생산 입고 상세 정보 조회
     * 
     * @param receiptNo 입고 번호
     * @return 입고 상세 정보
     */
    public Map<String, Object> getReceiptDetail(Long receiptNo);
    
}