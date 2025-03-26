package com.example.cmtProject.controller.erp.saleMgt;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.cmtProject.entity.erp.salesMgt.SalesOrder;
import com.example.cmtProject.entity.mes.standardInfoMgt.Clients;
import com.example.cmtProject.repository.erp.saleMgt.ClientsRepository;
import com.example.cmtProject.repository.erp.saleMgt.SalesOrderRepository;

@Controller
@RequestMapping("/sales")
public class saleController {
	
	@Autowired
	private SalesOrderRepository salesOrderRepository;
	
	@Autowired
	private ClientsRepository clientsRepository;
	
	@GetMapping("/soform")
	public String salesOrderForm(Model model) {
		
		//수주 전체 목록
 		List<SalesOrder> allList = salesOrderRepository.findAll();
 		model.addAttribute("soModel", allList);
 		
 		//-수주 목록에 있는 제품-
 		//수주 목록에 있는 제품 코드를 가져와 중복 제거 후 제품에서 제품명 출력
 		//-- 수주테이블에 있는 제품 코드 --
 		List<String> pdtCode = salesOrderRepository.findByGetPdtCode();
 		Collections.sort(pdtCode);
 		model.addAttribute("pdtCode",pdtCode);
 		
 		//-수주 목록에 해당하는 거래처-
 		//수주 목록에 있는 거래처 코드
 		List<String> cltCode = salesOrderRepository.findByGetCltCode();
 		Collections.sort(cltCode);
 		model.addAttribute("cltCode",cltCode);
 		
		return "erp/salesMgt/salesOrderForm";
 		//return "erp/salesMgt/aaa";
	}
	
	@GetMapping("/getPdtName")
	@ResponseBody
	public String getPdtName(@RequestParam("pdtCode") String pdtCode) {
		
		pdtCode = pdtCode.replaceAll("\"", ""); 
		String pdtName = salesOrderRepository.findByGetPdtName(pdtCode);
 
		return pdtName;
	}
	
	//@PostMapping("/getCltName") //@RequestBody
	@GetMapping("/getCltName") //@RequestParam 
	@ResponseBody
	public String getCltName(@RequestParam("cltCode") String cltCode) {
		
		 cltCode = cltCode.replaceAll("\"", ""); 
		 String cltName = salesOrderRepository.findByGetCltName(cltCode);
		 
		 return cltName;
	}
		
	@GetMapping("/soregisterform")
	public String soregisterform(Model model) {
 		
		List<Clients> list = clientsRepository.findAll();
		
	 	System.out.println(list);
	 	
	 	model.addAttribute("cltList", list);
	 	
		return "erp/salesMgt/soRegisterForm";
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

