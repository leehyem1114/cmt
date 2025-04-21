package com.example.cmtProject.controller.mes.production;

import java.time.LocalDate;
import java.time.LocalTime;
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
import com.example.cmtProject.dto.mes.standardInfoMgt.BomInfoDTO;
import com.example.cmtProject.service.mes.production.LotService;
import com.example.cmtProject.service.mes.production.ProductionPrcService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/production")
public class ProductionPrcController {

	@Autowired
	private ProductionPrcService productionPrcService;
	
	@Autowired
	private LotService lotService;
	
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
	public List<BomInfoDTO> pdtCodeSelected(@RequestParam("pdtCode") String pdtCode,
	        @RequestParam("woCode") String woCode) {
		
		//모달에서 선택된 pdtCode로 BOM테이블에 있는 데이터를 재귀로 가겨오기
		List<BomInfoDTO> selectPdtCodeList = productionPrcService.selectPdtCodeList(pdtCode);
		
		log.info("selectPdtCodeList:"+selectPdtCodeList);
		
		//lot테이블에서 현재 lot_no의 최대값
		int lotNoMax = lotService.getLotNo(); 
		
		log.info(String.valueOf(lotNoMax));
		
		//시작 버튼을 누르면 LOT테이블에 데이터가 입력된다
		for(BomInfoDTO bid : selectPdtCodeList) {
			
			lotNoMax++;
			int LOT_NO = lotNoMax;
			String LOT_CODE = makeLotCode(bid.getParentPdtCode());
			String CHILD_LOT_CODE = makeLotCode(bid.getChildItemCode());
			String PDT_CODE = bid.getParentPdtCode();
			String CHILD_PDT_CODE = bid.getChildItemCode();
			
			LocalDate today = LocalDate.now();
			String CREATE_DATE = String.valueOf(today);
			
			String PRC_TYPE = bid.getBomPrcType();
			
			String WO_CODE = woCode;
			
			LocalTime nowTime = LocalTime.now();
			String START_TIME = String.valueOf(nowTime);
			
			String WO_STATUS_NO = "RN";
			
			String USE_YN = "Y";
			
			LotDTO lotDto = new LotDTO();
		}
		
		return selectPdtCodeList;
	}
	
	private String makeLotCode(String code) {
		/*
		LocalDate todayDate = LocalDate.now();
		String today = String.valueOf(todayDate);
		
		//LOT번호 순서 추적(가장 뒤에 -001, -002 이 부분으로 같은 LOT번호 개수 가져오기)
		int LotNumOrder = lotService.getLotNumOrder();

				
		String lot = "L-" + today + ""
		*/
		return "";
				
	}
	
}
