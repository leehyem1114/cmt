package com.example.cmtProject.controller.erp.saleMgt;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.cmtProject.dto.erp.saleMgt.SalesOrderDTO;
import com.example.cmtProject.entity.comm.CommoncodeDetail;
import com.example.cmtProject.entity.erp.employees.Employees;
import com.example.cmtProject.entity.erp.salesMgt.SalesOrder;
import com.example.cmtProject.entity.erp.salesMgt.SalesOrderStatus;
import com.example.cmtProject.entity.mes.standardInfoMgt.Clients;
import com.example.cmtProject.entity.mes.standardInfoMgt.Products;
import com.example.cmtProject.repository.erp.employees.EmployeesRepository;
import com.example.cmtProject.repository.erp.saleMgt.ClientsRepository;
import com.example.cmtProject.repository.erp.saleMgt.CommoncodeDetailRepository;
import com.example.cmtProject.repository.erp.saleMgt.SalesOrderRepository;
import com.example.cmtProject.repository.erp.saleMgt.SalesOrderStatusRepository;
import com.example.cmtProject.repository.mes.standardInfoMgt.ProductsRepository;
import com.example.cmtProject.service.erp.saleMgt.SalesOrderService;

@Controller
@RequestMapping("/sales")
public class saleController {

	@Autowired
	private ProductsRepository productsRepository;
	
	@Autowired
	private SalesOrderRepository salesOrderRepository;
	
	@Autowired
	private ClientsRepository clientsRepository;
	
	@Autowired
	private EmployeesRepository employeesRepository;
	
	@Autowired
	private CommoncodeDetailRepository commoncodeDetailRepository;
	
	@Autowired
	private SalesOrderService salesOrderService;
	
	@Autowired
	private SalesOrderStatusRepository salesOrderStatusRepository;
	
	//조회 페이지
	@GetMapping("/soform")
	public String salesOrderForm(Model model) {
		
		//수주 전체 목록
 		List<SalesOrder> allList = salesOrderRepository.findAll();
 		model.addAttribute("soModel", allList);
 		
		//수주 메인 목록(clients, products, warehouses, employees 조인)
 		List<SalesOrderDTO> soMainList = salesOrderService.soMainSelect();
 		model.addAttribute("soMainList",soMainList);
 		//System.out.println(soMainList);

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
	
	//상품코드에 해당하는 상품명 가져오기
	@GetMapping("/getPdtName")
	@ResponseBody
	public String getPdtName(@RequestParam("pdtCode") String pdtCode) {
		
		pdtCode = pdtCode.replaceAll("\"", ""); 
		String pdtName = salesOrderRepository.findByGetPdtName(pdtCode);
 
		return pdtName;
	}
	
	//거래처 코드에 해당하는 거래처명 가져오기
	//@PostMapping("/getCltName") //@RequestBody
	@GetMapping("/getCltName") //@RequestParam 
	@ResponseBody
	public String getCltName(@RequestParam("cltCode") String cltCode) {
		
		 cltCode = cltCode.replaceAll("\"", ""); 
		 String cltName = salesOrderRepository.findByGetCltName(cltCode);
		 
		 return cltName;
	}
	
	//수주 등록 창
	@GetMapping("/soregisterform")
	public String soregisterform(Model model) {
 		
		List<Clients> cltList = clientsRepository.findAll();
		List<Employees> empList = employeesRepository.findAll();
		List<Products> productList = productsRepository.findAll();
		List<SalesOrderStatus> soStatusList = salesOrderStatusRepository.findAll();
		System.out.println("soStatusList:"+soStatusList);
		
	 	//수주번호 다음 시쿼스 가져오기
		Long nextSeq = salesOrderRepository.getNextSalesOrderNextSequences();
		
		//날짜 형태를 yyyyMMdd 헝태로 변경
		LocalDate today = LocalDate.now();        
        DateTimeFormatter todayFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
        String soToday = today.format(todayFormat);
        
        //수주코드 작성
		Long nextSoCodeNumber = salesOrderRepository.getNextSoCode();
		String soCode = "";
		if(nextSoCodeNumber > 100) {
			soCode = "SO-" + soToday + "-" + nextSoCodeNumber; 
		}else if(nextSoCodeNumber > 10) {
			soCode = "SO-" + soToday + "-" + "0" +nextSoCodeNumber;		
		}else if(nextSoCodeNumber > 0) {
			soCode = "SO-" + soToday + "-" + "00" +nextSoCodeNumber;
		}
		
		//공통코드에서 부서명 가져오기
		List<CommoncodeDetail> commListDetp = commoncodeDetailRepository.findByCmnCode("DEPT");
		
		//공통코드에서 직급명 가져오기
		List<CommoncodeDetail> commListPosition = commoncodeDetailRepository.findByCmnCode("POSITION");
		
		//SalesOrderStatus의 enum값 가져오기
		/*
		 * List<SalesOrderStatus> soStatus = Arrays.asList(SalesOrderStatus.values());
		 * System.out.println("soStatus:"+soStatus);
		 */
	 	
	 	model.addAttribute("cltList", cltList); //회사 정보
	 	model.addAttribute("empList", empList); //사원 정보
	 	model.addAttribute("productList",productList); //제품 정보
	 	model.addAttribute("soStatusList", soStatusList);
	 	model.addAttribute("nextSeq", nextSeq); //수주 번호
	 	model.addAttribute("soCode", soCode); //수주 코드
	 	model.addAttribute("commListDetp",commListDetp); //공통코드에서 부서
	 	model.addAttribute("commListPosition",commListPosition); //공콩코드에서 직급
	 	
	 	//th:object에서 사용할 객체 생성
	 	model.addAttribute("salesOrder", new SalesOrder());
	 	
		return "erp/salesMgt/soRegisterForm";
	}
	
	@PostMapping("/soregister")
	public String soRegister(@ModelAttribute SalesOrder salesOrder) {
		
		System.out.println("salesOrder:"+ salesOrder);
		
		return "";
	}
	
	@GetMapping("/getEmpName")
	@ResponseBody
	public List<Employees> getEmpName(@RequestParam("deptCode") Long deptNo1,
							@RequestParam("posCode") Long positionNo) {
		
		List<Employees> empList = employeesRepository.getEmpName(deptNo1, positionNo);
		
		return empList;
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

