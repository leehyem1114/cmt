package com.example.cmtProject.controller.mes.warehouse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.example.cmtProject.constants.PathConstants;
import com.example.cmtProject.service.mes.warehouse.WarehouseService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 *  창고 화면 컨트롤러
 *  사용자에게 보여질 화면을 처리하는 컨트롤러
 * 
 */
@Controller
@RequestMapping(PathConstants.WAREHOUSE_BASE)
@Slf4j
public class WarehouseController {
	
	@Autowired
	private WarehouseService whs;
	
	
	/**
	 * 
	 * 창고 관리 메인 페이지 
	 * 초기 정보를 모델에 담아 뷰로 전달 
	 * 
	 */
	@GetMapping(PathConstants.WAREHOUSE_VIEW)
	public String warehouseListGET(Model model) {
		
		Map<String,Object> findMap = new HashMap<>();
		List<Map<String, Object>> wareHouseList = whs.warehouseList(findMap);
		model.addAttribute("wareHouseList", wareHouseList);
		
		
		
		return PathConstants.VIEW_WAREHOUEW_VIEW;
		
	}
	

	
	
}
