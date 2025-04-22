package com.example.cmtProject.mapper.mes.inventory;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface MaterialReceiptMapper {
	
	/**
	 * 발주 리스트 조회(Map 반환)
	 */
	
	public List<Map<String, Object>> puchasesList(Map<String,Object>map);
	
	/**
	 * 입고 리스트 조회(Map 반환)
	 */
	
	public List<Map<String, Object>> mReceiptList(Map<String,Object>map);
	
	/**
	 * 입고정보 삽입
	 */
	
	public int insertMaterialReceipt(Map<String, Object> params);

}
