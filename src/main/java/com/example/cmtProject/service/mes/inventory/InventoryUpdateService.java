package com.example.cmtProject.service.mes.inventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.cmtProject.mapper.mes.inventory.InventoryUpdateMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class InventoryUpdateService {
	
	@Autowired
	private InventoryUpdateMapper Ium;
	
	
	/**
	 * 검수 완료 여부 업데이트
	 */
	
	/**
	 * 
	 * 생산계획 대비 자재 업데이트
	 */
    @Transactional
    public void updateAllocatedQuantities(Map<String, Object> params) {
        // 공통 BOM 정보 조회
        List<Map<String, Object>> bomItems = Ium.getBomItems(params);
        params.put("bomItems", bomItems);  // 조회한 BOM 정보를 파라미터에 추가
        
        // 1. 자재 재고 업데이트
        Ium.updateMaterialAllocatedQty(params);
        
        // 2. 제품 재고 업데이트
        Ium.updateProductAllocatedQty(params);
    }

}
