package com.example.cmtProject.service.mes.inventory;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.cmtProject.mapper.mes.inventory.MaterialInventoryMapper;

@Service
public class MaterialInventoryService {
	
	@Autowired
	private MaterialInventoryMapper mImapper;
	
	public List<Map<String,Object>> inventoryList(Map<String, Object>map){
		
		return mImapper.mInventoryList(map);
		
	}

}
