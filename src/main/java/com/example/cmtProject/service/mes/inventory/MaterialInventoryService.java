package com.example.cmtProject.service.mes.inventory;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.cmtProject.mapper.mes.inventory.MaterialInventoryMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MaterialInventoryService {
	
	@Autowired
	private MaterialInventoryMapper mImapper;
	
	public List<Map<String,Object>> inventoryList(Map<String, Object>map){
		
		return mImapper.mInventoryList(map);
		
	}

}
