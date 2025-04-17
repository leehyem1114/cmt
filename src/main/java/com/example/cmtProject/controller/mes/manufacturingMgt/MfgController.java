package com.example.cmtProject.controller.mes.manufacturingMgt;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.cmtProject.dto.erp.employees.EmpListPreviewDTO;
import com.example.cmtProject.dto.mes.standardInfoMgt.ProductTotalDTO;
import com.example.cmtProject.entity.erp.salesMgt.SalesOrder;
import com.example.cmtProject.repository.erp.saleMgt.SalesOrderRepository;
import com.example.cmtProject.service.erp.employees.EmployeesService;
import com.example.cmtProject.service.mes.manufacturingMgt.MfgService;
import com.example.cmtProject.service.mes.standardInfoMgt.ProductService;

@Controller
@RequestMapping("/mfg")
public class MfgController {
	
	@Autowired
	private MfgService mfgService;
	
	@Autowired
	private SalesOrderRepository salesOrderRepository;
	
	@Autowired
	private EmployeesService employeesService;
	
	@Autowired
	private ProductService productService;
	
	@GetMapping("/mfg-plan")
	public String mfgPlan(Model model) throws Exception {
		
		// 수주
		List<SalesOrder> soList = salesOrderRepository.findAll();
		model.addAttribute("soList", soList);
		
		// 제품
		List<ProductTotalDTO> pdtList = productService.getProductTotalList();
		model.addAttribute("pdtList", pdtList);
		
		// 사원
		List<EmpListPreviewDTO> empList = employeesService.getEmpList();
		model.addAttribute("empList", empList);
		
		return "mes/manufacturingMgt/mfgPlan";
	}
	
	@GetMapping("/mfg-schedule")
	public String mfgSchedule() {
		
		return "mes/manufacturingMgt/mfgSchedule";
	}
	
	@GetMapping("/mfg-history")
	public String mfgHistory() {
		
		return "mes/manufacturingMgt/mfgHistory";
	}
	

}
