package com.example.cmtProject.mapper.mes.inventory;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface MaterialInventoryMapper {
	
	
	/**
	 * 
	 * 원자재 재고조회(Map 반환)
	 * 
	 */
	
	public List<Map<String, Object>> mInventoryList(Map<String,Object>map);

}
