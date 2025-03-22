package com.example.cmtProject.controller.erp.saleMgt;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/sale")
public class saleController {
	
	@GetMapping("/order")
	public String order() {
		
		return "erp/saleMgt/order";
	}
	
	@GetMapping("/receive")
	public String receive() {
		
		return "erp/saleMgt/receive";
	}
	
	@GetMapping("/shipment")
	public String shipment() {
		
		return "erp/saleMgt/shipment";
	}
	
	@GetMapping("/chart")
	public String chart() {
		
		return "erp/saleMgt/chart";
	}
	
	@GetMapping("/modal")
	public String modal() {
		
		return "erp/saleMgt/modal";
	}
	
	@GetMapping("/grid")
	public String grid() {
		
		return "erp/saleMgt/grid";
	}
}
