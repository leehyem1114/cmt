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

import com.example.cmtProject.dto.erp.employees.EmpListPreviewDTO;
import com.example.cmtProject.dto.erp.saleMgt.SalesOrderDTO;
import com.example.cmtProject.dto.erp.saleMgt.SalesOrderMainDTO;
import com.example.cmtProject.dto.mes.manufacturingMgt.MfgPlanDTO;
import com.example.cmtProject.dto.mes.manufacturingMgt.MfgScheduleDTO;
import com.example.cmtProject.dto.mes.standardInfoMgt.ProductTotalDTO;
import com.example.cmtProject.entity.erp.salesMgt.SalesOrder;
import com.example.cmtProject.repository.erp.saleMgt.SalesOrderRepository;
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
	
	// 생산 계획 목록 조회
	@GetMapping("/mfg-plan")
	public String mfgPlan(Model model) {
		
		// 생산 계획
		List<MfgPlanDTO> mpList = mfgService.getMfgPlanTotalList();
		model.addAttribute("mpList", mpList);
		
		// 수주
//		List<SalesOrder> soList = salesOrderRepository.findAll();
//		model.addAttribute("soList", soList);
		
		// 제품
//		List<ProductTotalDTO> pdtList = productService.getProductTotalList();
//		model.addAttribute("pdtList", pdtList);
		
		// 사원
//		List<EmpListPreviewDTO> empList = employeesService.getEmpList();
//		model.addAttribute("empList", empList);
		
		return "mes/manufacturingMgt/mfgPlan";
	}
	
	@PostMapping("/soList")
	@ResponseBody
	public List<SalesOrderMainDTO> getSoList() {
		return salesOrderService.soMainSelect();
	}
	
	@GetMapping("/mfgPlanRegi")
	public String mfgPlan() {
	    return "mfg-plan";
	}
	
	@GetMapping("/mfg-schedule")
	public String mfgSchedule(Model model) {
		// 제조 계획
		List<MfgScheduleDTO> msList = mfgService.getMfgScheduleTotalList();
		model.addAttribute("msList", msList);
		
		return "mes/manufacturingMgt/mfgSchedule";
	}
	
	@GetMapping("/mfg-history")
	public String mfgHistory() {
		
		return "mes/manufacturingMgt/mfgHistory";
	}
	

}
