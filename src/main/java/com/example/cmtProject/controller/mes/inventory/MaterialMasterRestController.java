package com.example.cmtProject.controller.mes.inventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.cmtProject.comm.response.ApiResponse;
import com.example.cmtProject.constants.PathConstants;
import com.example.cmtProject.service.mes.inventory.MaterialMasterService;

import lombok.extern.slf4j.Slf4j;

@RestController
//@RequestMapping()
@Slf4j
public class MaterialMasterRestController {
    
    @Autowired
    private MaterialMasterService mms;
    
    /**
     * 
     * 원자재 기준정보 메인 페이지 API 
     * 
     * 
     */
    @GetMapping("/api/~~")
    public ApiResponse<List<Map<String, Object>>> getMaterial(
    		@RequestParam(name = "keyword", required = false) String keyword){
    	
        Map<String, Object> findMap = new HashMap<>();
        // 사용자가 입력한 검색어(keyword)가 null이 아니고, 공백만으로 구성되어 있지 않은 경우에만 실행
        if (keyword != null && !keyword.trim().isEmpty()) {
            // 검색어를 Map에 추가 (이후 검색 조건으로 사용하기 위함)
            findMap.put("keyword", keyword);
        }
    	
    	List<Map<String, Object>> materialList = mms.materialList(findMap);
    	return ApiResponse.success(materialList);
    }
    
    /**
     * 
     * 원자재 정보 단건 조회
     * 
     */
   
    /**
     * 
     * 원자재 정보 단건 저장(등록 /수정)
     * 
     */
    
    /**
     * 
     * 원자재 정보 일괄 저장API
     * 
     */
    
    /**
     * 
     * 원자재 정보 삭제 API
     * 
     */
    
}