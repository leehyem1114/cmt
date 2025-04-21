package com.example.cmtProject.controller.mes.production;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.cmtProject.dto.mes.production.WorkOrderDTO;
import com.example.cmtProject.dto.mes.standardInfoMgt.BomInfoDTO;
import com.example.cmtProject.service.mes.production.ProductionPrcService;

@Slf4j
@Controller
@RequestMapping("/production")
public class ProductionPrcController {

	@Autowired
	private ProductionPrcService productionPrcService;
	
	//공정 현황 메인 페이지
	@GetMapping("/productionPrc")
	public String getMethodName(Model model) {
		
		//작업상태가 StandBy인 것만 가져온다(모달 셀렉트 박스에서 workOrderSBList 사용)
		List<WorkOrderDTO> workOrderSBList = productionPrcService.selectWoStandByList();
		//log.info("workOrderList:"+workOrderSBList);
		
		model.addAttribute("workOrderSBList", workOrderSBList);
		
		return "mes/production/productionPrc";
	}
	
	//모달 셀렉트 박스에서 실행
	@GetMapping("/woCodeSelected")
	@ResponseBody
	public List<WorkOrderDTO> woCodeSelected(@RequestParam("data") String data) {
		
		//모달 셀렉트 박스에서 선택된 woCode에 해당하는 데이터를 가져온다 
		List<WorkOrderDTO> selectWoCodeList = productionPrcService.selectWoCodeList(data);
		
		//log.info("selectWoCodeList:"+selectWoCodeList);
		return selectWoCodeList;
	}
	
	//모달 시작 버튼에서 실행
	@GetMapping("/pdtCodeSelected")
	@ResponseBody
	public List<BomInfoDTO> pdtCodeSelected(@RequestParam("data") String data) {
		
		//모달에서 선택된 pdtCode로 BOM테이블에 있는 데이터를 재귀로 가겨오기
		List<BomInfoDTO> selectPdtCodeList = productionPrcService.selectPdtCodeList(data);
		
		log.info("selectPdtCodeList:"+selectPdtCodeList);
		return selectPdtCodeList;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
}
