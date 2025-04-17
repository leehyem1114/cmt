package com.example.cmtProject.mapper.mes.wareHouse;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 *  창고관리 매퍼 인터페이스
 * 
 */

@Mapper
@Repository
public interface WareHouseMapper {
	
	/**
	 * 
	 * 창고 목록 조회(Map 반환)
	 * 
	 */
	public List<Map<String, Object>> warehouseList(Map<String, Object> map);
		

}
