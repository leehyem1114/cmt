package com.example.cmtProject.controller.mes.inventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.cmtProject.comm.response.ApiResponse;
import com.example.cmtProject.service.mes.inventory.MaterialInventoryService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/materialInventory")
@Slf4j
 
public class MaterialInventoryRestController {
	
	@Autowired
	private MaterialInventoryService mis;
	
	
    /**
     * 원자재 재고 목록 조회 API
     * 
     * @param keyword 검색 키워드 (선택사항)
     * @return 재고 목록 데이터
     */
	
	@GetMapping("/list")
	public ApiResponse<List<Map<String, Object>>> getmInventory(
			@RequestParam(name = "keyword", required = false) String keyword) {
		
		Map<String, Object> findMap = new HashMap<>();
	     // 사용자가 입력한 검색어(keyword)가 null이 아니고, 공백만으로 구성되어 있지 않은 경우에만 실행
        if (keyword != null && !keyword.trim().isEmpty()) {
        	// 검색어를 Map에 추가 (이후 검색 조건으로 사용하기 위함)
            findMap.put("keyword", keyword);
        }
        log.info("재고 목록 조회 요청. 검색어: {}", keyword);
        List<Map<String, Object>> mInventory = mis.inventoryList(findMap);
        log.info("재고 목록 조회 결과: {}건", mInventory.size());
		
		return ApiResponse.success(mInventory);
		
	}
	
    /**
     * 원자재 재고 차감 API (FIFO 적용)
     * 공정에서 사용된 자재의 재고를 FIFO 방식으로 차감합니다.
     * 
     * @param params 차감 정보 (mtlCode: 자재코드, consumptionQty: 소비량, updatedBy: 처리자)
     * @return 처리 결과
     */
    @PostMapping("/consume")
    public ApiResponse<Map<String, Object>> consumeMaterial(@RequestBody Map<String, Object> params) {
        log.info("자재 재고 차감 요청: {}", params);
        
        Map<String, Object> result = mis.consumeMaterialFIFO(params);
        
        if ((Boolean) result.get("success")) {
            log.info("자재 재고 차감 성공: {}", result.get("message"));
            return ApiResponse.success(result);
        } else {
            log.warn("자재 재고 차감 실패: {}", result.get("message"));
            return ApiResponse.error(result.get("message").toString(), result);
        }
    }
}