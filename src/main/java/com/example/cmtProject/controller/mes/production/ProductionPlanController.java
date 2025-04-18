package com.example.cmtProject.controller.mes.production;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.cmtProject.dto.mes.production.WorkOrderDTO;
import com.example.cmtProject.service.mes.production.WorkOrderService;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Controller
//@RequestMapping("")
public class ProductionPlanController { //생산계획 수립, 작업지시 발행, 공정순서
	@Autowired WorkOrderService orderService;
	
	//생산계획 리스트
	@GetMapping("/workOrder")
	public String workOrder(WorkOrderDTO workOrderDTO,Model model) {
		List<WorkOrderDTO> orderList = orderService.getOrderList();
		model.addAttribute("orderList",orderList);
		log.info(">>"+orderList);

		return"mes/production/work_order";
	}
	
	
}
