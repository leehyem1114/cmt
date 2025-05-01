package com.example.cmtProject.service.mes.inventory;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.cmtProject.mapper.mes.inventory.InventoryUpdateMapper;
import com.example.cmtProject.util.SecurityUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class InventoryUpdateService {
	
	@Autowired
	private InventoryUpdateMapper Ium;
	
	/**
	 * 생산계획 대비 자재/제품 할당 수량 업데이트
	 * @param params soCode - 판매주문 코드, soQty - 주문 수량, updatedBy - 수정자
	 */
	@Transactional
	public void updateAllocatedQuantities(Map<String, Object> params) {
	    String soCode = (String) params.get("soCode");
	    Long soQty = (Long) params.get("soQty");
	    
	    // 현재 사용자 ID 가져오기
	    String userId = SecurityUtil.getUserId();
	    params.put("updatedBy", userId);
	    
	    log.info("재고 할당 시작 - 주문코드: {}, 주문수량: {}", soCode, soQty);
	    
	    // BOM 항목 조회
	    List<Map<String, Object>> bomItems = Ium.getBomItems(params);
	    log.info("BOM 항목 조회: 총 {}개", bomItems.size());
	    
	    // BOM 항목 로깅
	    for (Map<String, Object> item : bomItems) {
	        log.info("아이템: {} (타입: {}, 필요수량: {} * {} = {})", 
	            item.get("PARENT_PDT_CODE"), 
	            item.get("ITEM_TYPE"),
	            item.get("BOM_QTY"), 
	            soQty,
	            Double.parseDouble(item.get("BOM_QTY").toString()) * soQty);
	    }
	    
	    // 1. 자재 재고 업데이트
	    int materialUpdated = Ium.updateMaterialAllocatedQty(params);
	    log.info("자재 재고 업데이트: {} 건", materialUpdated);
	    
	    // 2. 제품 재고 업데이트
	    int productUpdated = Ium.updateProductAllocatedQty(params);
	    log.info("제품 재고 업데이트: {} 건", productUpdated);
	}
}