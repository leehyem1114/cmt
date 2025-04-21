package com.example.cmtProject.controller.mes.production;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.cmtProject.dto.mes.production.LotDTO;
import com.example.cmtProject.dto.mes.production.WorkOrderDTO;
import com.example.cmtProject.service.mes.production.WorkOrderService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/production")
public class ProductionExecutionController {//	생산 중 실적 등록, 자재 투입, LOT 추적
	@Autowired WorkOrderService orderService;
	
	//Lot추적
	@GetMapping("/lotTracking")
	public String lotTracking(Model model) {
		//로트번호로 단일제품 번호 불러오기
//		WorkOrderDTO product = orderService.getProductDetail(lotNo);
//		model.addAttribute("product",product);
		
		//lot + work_order 테이블 들고오기
		List<LotDTO> orderList = orderService.getLotDetail();
		model.addAttribute("orderList",orderList);
		
		return "mes/production/lotTracking";
	}
	
	@GetMapping("/lotDetail")
	@ResponseBody
	public LotDTO lotDetail(@RequestParam("lotNo") Long lotNo, Model model) {
//		LotDTO detail = orderService.getLotNoDetail(lotNo);
//		 model.addAttribute("order", detail);
		return orderService.getLotNoDetail(lotNo);
	}
}
