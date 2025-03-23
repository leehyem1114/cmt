package com.example.cmtProject.controller.erp.saleMgt;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/sales")
public class saleController {
	
	@GetMapping("/soform")
	public String salesOrderForm() {
		
		
		
		return "erp/salesMgt/salesOrderForm";
	}
	
	
	@GetMapping("/so")
	public String salesOrder() {
		
		return "redirect:/";
	}
	
	@GetMapping("/poform")
	public String purchaseOrderForm() {
		
		return "erp/salesMgt/purchaseOrderForm";
	}
	
	
	@GetMapping("/po")
	public String purchaseOrder() {
		
		return "redicrect:/";
	}
	
	@GetMapping("/shipment")
	public String shipment() {
		
		return "erp/salesMgt/shipment";
	}
	
	@GetMapping("/chart")
	public String chart() {
		
		return "erp/salesMgt/chart";
	}
	
	@GetMapping("/modal")
	public String modal() {
		
		return "erp/salesMgt/modal";
	}
	
	@GetMapping("/grid")
	public String grid() {
		
		return "erp/salesMgt/grid";
	}
	
	@GetMapping("/baseSale")
	public String baseSale() {
		
		return "erp/salesMgt/baseSale";
	}
}
