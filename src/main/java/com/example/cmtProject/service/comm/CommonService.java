package com.example.cmtProject.service.comm;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.cmtProject.mapper.common.CommonCodeMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CommonService {
	@Autowired
	private CommonCodeMapper commonmapper ;
	
	/*
	 * 공통코드 조회
	 * 
	 */
    public List<Map<String, Object>> commonList(Map<String, Object> map) {
//        log.info("CommonService: commonList 메서드 호출됨");
//        log.info("파라미터: {}", map);
        
        List<Map<String, Object>> result = commonmapper.commonList(map);
        
//        log.info("조회 결과 개수: {}", (result != null ? result.size() : "null"));
//        if (result != null && !result.isEmpty()) {
//            log.info("첫 번째 항목: {}", result.get(0));
//        } else {
//            log.info("조회 결과가 없습니다.");
//        }
        return result;
    }
	/*
	 * 공통코드 상세조회
	 * 
	 */
    
    public List<Map<String, Object>> commonDetailList(Map<String, Object> map) {
//        log.info("CommonService: commonList 메서드 호출됨");
//        log.info("파라미터: {}", map);
    	
    	List<Map<String, Object>> result = commonmapper.commonDetailList(map);
    	
//        log.info("조회 결과 개수: {}", (result != null ? result.size() : "null"));
//        if (result != null && !result.isEmpty()) {
//            log.info("첫 번째 항목: {}", result.get(0));
//        } else {
//            log.info("조회 결과가 없습니다.");
//        }
    	return result;
    }
	

	
	
	
}//CommonService
