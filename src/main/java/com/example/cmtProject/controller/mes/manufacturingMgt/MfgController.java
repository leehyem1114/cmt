package com.example.cmtProject.controller.mes.manufacturingMgt;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.cmtProject.dto.mes.manufacturingMgt.MfgPlanDTO;
import com.example.cmtProject.dto.mes.manufacturingMgt.MfgPlanSalesOrderDTO;
import com.example.cmtProject.dto.mes.manufacturingMgt.MfgScheduleDTO;
import com.example.cmtProject.dto.mes.manufacturingMgt.MfgSchedulePlanDTO;
import com.example.cmtProject.service.erp.employees.EmployeesService;
import com.example.cmtProject.service.erp.saleMgt.SalesOrderService;
import com.example.cmtProject.service.mes.manufacturingMgt.MfgService;
import com.example.cmtProject.service.mes.standardInfoMgt.ProductService;

@Controller
@RequestMapping("/mfg")
public class MfgController {
	
	@Autowired
	private MfgService mfgService;
	
	@Autowired
	private SalesOrderService salesOrderService;
	
	@Autowired
	private EmployeesService employeesService;
	
	@Autowired
	private ProductService productService;
	
	
	// 생산 계획 조회
	@GetMapping("/mfg-plan")
	public String mfgPlan(Model model) {
		
		// 생산 계획
		List<MfgPlanDTO> mpList = mfgService.getMfgPlanTotalList();
		model.addAttribute("mpList", mpList);
		
		// 수주
		List<MfgPlanSalesOrderDTO> soList = mfgService.getSoList();
		model.addAttribute("soList", soList);
		
		// 제품
//		List<ProductTotalDTO> pdtList = productService.getProductTotalList();
//		model.addAttribute("pdtList", pdtList);
		
		// 사원
//		List<EmpListPreviewDTO> empList = employeesService.getEmpList();
//		model.addAttribute("empList", empList);
		
		return "mes/manufacturingMgt/mfgPlan";
	}
	
	// 생산 계획 등록 조회
	@GetMapping("/mfgPlanRegi")
	public String mfgPlanRegi(Model model) {
		List<MfgPlanSalesOrderDTO> soList = mfgService.getSoList();
		model.addAttribute("soList", soList);
		
		System.out.println("soList 확인 : " + soList);
		
	    return "mes/manufacturingMgt/mfgPlan";
	}
	
	// 완제품 재고 조회
//	@GetMapping("/selectCurrentQty")
//	@ResponseBody
//	public boolean selectCurrentQty(@RequestParam("pdtCode") String pdtCode,@RequestParam("soQuantity") Long soQuantity) {
//		boolean isAvailable = materialInventoryService.isCurrentQtyEnough(pdtCode, soQuantity);
//		return isAvailable;
//	}
	
	// 제조 계획 조회
	@GetMapping("/mfg-schedule")
	public String mfgSchedule(Model model) {
		// 제조 계획
		List<MfgScheduleDTO> msList = mfgService.getMfgScheduleTotalList();
		model.addAttribute("msList", msList);
		
		// 생산 계획
		List<MfgPlanDTO> mpList = mfgService.getMfgPlanTotalList();
		model.addAttribute("mpList", mpList);
		
		return "mes/manufacturingMgt/mfgSchedule";
	}
	
	// 제품 계획 등록 조회
	@GetMapping("/mfgScheduleRegi")
	public String mfgScheduleRegi(Model model) {
		List<MfgSchedulePlanDTO> mpList = mfgService.getMpList();
		model.addAttribute("mpList", mpList);
		
		System.out.println("mpList 확인 : " + mpList);
		
	    return "mes/manufacturingMgt/mfgPlan";
	}
	
	// 생산 이력 조회
	@GetMapping("/mfg-history")
	public String mfgHistory(Model model) {
		
		return "mes/manufacturingMgt/mfgHistory";
	}
	

}
