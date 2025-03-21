package com.example.cmtProject.controller.comm;

import java.security.Provider.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.cmtProject.service.comm.CommonService;

@Controller
@RequestMapping("/comm")
public class CommonCodeController {
	
	@Autowired
	private CommonService comservice;
	
	@GetMapping("/common")
	public String commonListGET(Model model) {
		// 메인 공통 코드 목록 가져오기
		Map<String, Object> findMap = new HashMap<>();
		List<Map<String, Object>> commonList = comservice.commonList(findMap);
		model.addAttribute("commonList", commonList);
		
		// 상세 공통 코드 목록 가져오기
	    Map<String, Object> findDeMap = new HashMap<>();
	    List<Map<String, Object>> commonDeList = comservice.commonDetailList(findDeMap);
	    model.addAttribute("commonDeList", commonDeList);
		
		return "comm/commonCode";
	}	
		
	
	
	
	

}// CommonCodeController
