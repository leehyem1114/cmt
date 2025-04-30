package com.example.cmtProject.service.mes.inventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.cmtProject.mapper.mes.inventory.InventoryUpdateMapper;
import com.example.cmtProject.mapper.mes.inventory.MaterialMasterMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MaterialMasterService {
	
	@Autowired
	private MaterialMasterMapper mm;
	
	/**
	 * 원자재 기존정보 목록 조회
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> materialList(Map<String, Object>param){
		log.info("materialList: materialList 호출. 파라미터: {} ", param);
		return mm.selectMaterials(param);
		
	}
	
	/**
	 * 원자재 기준정보 목록 단건 조회
	 * @param param
	 * @return
	 */
	public Map<String, Object> materialSingle(Map<String, Object>param){
		log.info("selectSingle: selectSingle 호출. 파라미터: {} ", param);
		return mm.selectSingleMaterials(param);
		
	}
	
//	public Map<String, Object> insertMaterials(Map<String, Object> params){
//		Map<String, Object> resultMap = new HashMap<>();
//		
//		
//		return "";
//	}

}