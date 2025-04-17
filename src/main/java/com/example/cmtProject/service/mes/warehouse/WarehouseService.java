package com.example.cmtProject.service.mes.warehouse;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.cmtProject.mapper.mes.wareHouse.WareHouseMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WarehouseService {
	
	@Autowired
	private WareHouseMapper wareHouseMapper;
	
	public List<Map<String,Object>> warehouseList(Map<String, Object>map){
		log.info("warehouseList: warehouseList 호출. 파라미터: {} ", map);
		return wareHouseMapper.warehouseList(map);
		
	}
}
