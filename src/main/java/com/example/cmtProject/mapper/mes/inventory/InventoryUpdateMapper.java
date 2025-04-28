package com.example.cmtProject.mapper.mes.inventory;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface InventoryUpdateMapper {
	
	/**
	 *입고 상태 업데이트
	 */
	public int updateReceiptStatus(Map<String, Object> params);
	
	/**
	 * 생산계획 대비 계획재고 업데이트
	 */
	public int updateAllocatedQtyWithMerge(Map<String, Object> params);
	
}
