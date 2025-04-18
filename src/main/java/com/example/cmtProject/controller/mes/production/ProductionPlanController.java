package com.example.cmtProject.controller.mes.production;

import java.util.List;

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

		return"mes/production/work_order";
	}
	
	@PostMapping("/workOrder/regist")
	@ResponseBody
	public String regiWorkOrderLsit(@RequestBody WorkOrderDTO workOrderDTO) {
		//작업지시 등록
		orderService.registMsPlan(workOrderDTO);
		//제조계획상태 업데이트
		orderService.updateMfgStatus(workOrderDTO.getMsNo());
		log.info("받은 데이터" + workOrderDTO);
		return "리스트 업뎃 완";
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
