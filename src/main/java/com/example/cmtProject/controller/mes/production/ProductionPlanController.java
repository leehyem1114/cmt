package com.example.cmtProject.controller.mes.production;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.annotation.RequestScope;

import com.example.cmtProject.dto.mes.manufacturingMgt.MfgScheduleDTO;
import com.example.cmtProject.dto.mes.production.WorkOrderDTO;
import com.example.cmtProject.service.mes.production.WorkOrderService;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Controller
//@RequestMapping("")
public class ProductionPlanController { //생산계획 수립, 작업지시 발행, 공정순서
	@Autowired WorkOrderService orderService;
	
	//작업지시 리스트
	@GetMapping("/workOrder")
	public String workOrderList(WorkOrderDTO workOrderDTO,Model model) {
		//작업지시
		List<WorkOrderDTO> orderList = orderService.getOrderList();
		model.addAttribute("orderList",orderList);
		log.info(">>"+orderList);

		//제조계획 리스트
		List<MfgScheduleDTO> planList = orderService.getPlanList();
		model.addAttribute("planList",planList);
		
		List<WorkOrderDTO> stats = orderService.getCompleteStatsLast7Days();


		List<String> workDateList = stats.stream()
		    .map(WorkOrderDTO::getWorkDate)
		    .collect(Collectors.toList());
	
		List<Integer> completeCountList = stats.stream()
		    .map(WorkOrderDTO::getCompleteCount)
		    .collect(Collectors.toList());


	    model.addAttribute("workDateList", workDateList);
	    model.addAttribute("completeCountList", completeCountList);
	    
		return"mes/production/work_order";
	}
	
	//작업지시 데이터만 JSON으로 내려주는 API
	@GetMapping("/workOrder/data")
	@ResponseBody
	public List<WorkOrderDTO> getWorkOrderData() {
	    return orderService.getOrderList();
	}
	
	@PostMapping("/workOrder/regist")
	@ResponseBody
	public String regiWorkOrderLsit(@RequestBody WorkOrderDTO workOrderDTO) {

		//작업지시 등록
		Long woNo = orderService.getWoNoMax();
		String woCodeLast = orderService.getWoCodeLast();
		String woCode = changeWoCode(woCodeLast);

		System.out.println("woNo" + woNo);
		System.out.println("woCodeLast" + woCodeLast);
		System.out.println("woCode" + woCode);
		
		workOrderDTO.setWoNo(woNo+1);
		workOrderDTO.setWoCode(woCode);
		
		orderService.registMsPlan(workOrderDTO);
		
		//제조계획상태 업데이트 &제조 계획리스트에서 삭제
		orderService.updateMfgStatus(workOrderDTO.getMsNo());
		
		log.info("받은 데이터" + workOrderDTO);
		return "작업지시 추가 완료";
	}
	
	public String changeWoCode(String code) {
		
		String numberPart = code.substring(3);
        int number = Integer.parseInt(numberPart) + 1;
        
        String chCode = "MSC";
        
        if(number >= 100) {
        	chCode += Integer.toString(number); 
        }else if(number >= 10) {
        	chCode += "0" + Integer.toString(number);
        }else if(number >= 0) {
        	chCode += "00" + Integer.toString(number);
        }else {
        	chCode = "error";
        }
		
		return chCode;
	}
	
	//공정현황**********************************
	@GetMapping("/process")
	public String process(Model model) {
		List<WorkOrderDTO> orderList = orderService.getOrderList();
		model.addAttribute("orderList",orderList);
		return"mes/production/processList";
	}
	//작업시작 버튼 = 날짜 업데이트&진행중
	@PostMapping("/workOrder/start")
	@ResponseBody
	public String workOrderStart(@RequestBody WorkOrderDTO workOrderDTO) {
		orderService.startWork(workOrderDTO.getWoNo());
		return "날짜 업데이트";
	}
	

	
}
