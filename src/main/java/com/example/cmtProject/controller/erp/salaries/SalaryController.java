package com.example.cmtProject.controller.erp.salaries;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.MapContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.cmtProject.dto.comm.CommonCodeDetailDTO;
import com.example.cmtProject.dto.comm.CommonCodeDetailNameDTO;
import com.example.cmtProject.dto.erp.employees.EmpListPreviewDTO;
import com.example.cmtProject.dto.erp.salaries.PayPositionDTO;
import com.example.cmtProject.dto.erp.salaries.PaySearchDTO;
import com.example.cmtProject.dto.erp.salaries.PaymentDTO;
import com.example.cmtProject.service.comm.CommonService;
import com.example.cmtProject.service.erp.employees.EmployeesService;
import com.example.cmtProject.service.erp.salaries.SalaryService;


@Controller
@RequestMapping("/salaries")
public class SalaryController {
	@Autowired
	private SalaryService salaryService;
	@Autowired
	private EmployeesService employeesService;
	@Autowired
	private CommonService commonService;

	// 급여 지급 내역 조회
	@GetMapping("/payList")
	public String getPayList(Model model ){

		model.addAttribute("paySearchDTO", new PaySearchDTO());
		commonCodeName(model,commonService);
		
		List<PaymentDTO> payList = salaryService.getPayList();
		model.addAttribute("payList", payList);
		
		
		//System.out.println("payList:"+payList);
		
		// 공통 코드에서 가져오기
		List<CommonCodeDetailNameDTO> deptList = commonService.getCodeListByGroup("DEPT");
		model.addAttribute("deptList", deptList);
		
		List<EmpListPreviewDTO> empList = employeesService.getEmplist();
		model.addAttribute("empList", empList);
		//System.out.println("사원 목록 확인 : " + empList);
		
		List<CommonCodeDetailDTO> payDay = commonService.getCommonCodeDetails("PAYDAY", null);
		model.addAttribute("payDay", payDay);
		
		return "erp/salaries/payList";
	}
	
	// 급여 지급 내역 검색 요청
	@GetMapping("/searchPayList")
	public String getSearchPay(PaySearchDTO paySearchDTO, Model model) {
		System.out.println("검색 대상 : " + paySearchDTO);
		
		List<PaySearchDTO> paySearchList = salaryService.getSearchPayList(paySearchDTO);
//		model.addAttribute("paySearchList", paySearchList);
		model.addAttribute("payList", paySearchList);
		
		model.addAttribute("paySearchDTO", paySearchDTO);
		
		List<CommonCodeDetailNameDTO> deptList = commonService.getCodeListByGroup("DEPT");
		model.addAttribute("deptList", deptList);
		
		List<EmpListPreviewDTO> empList = employeesService.getEmplist();
		model.addAttribute("empList", empList);
		
		return "erp/salaries/payList";
	}
	
	// 급여계산기
	@GetMapping("/payCalculator")
	public String getPayCalc() {
		return "erp/salaries/payList";
	}	
	
	// 급여 이체
	@PostMapping("/payTransfer")
	@ResponseBody
	public String payTransfer(@RequestParam("position") String position, @RequestParam("empNoList[]") List<String> empNoList, Model model) {	
		
		System.out.println("position:"+position+" ,empNoList:"+empNoList);
		
		// 급여 지급일 조회		
		CommonCodeDetailDTO payTransferDay = commonService.getCommonCodeDetail("PAYDAY", "PDY001");
		
		System.out.println("---------------------------------payTransferDay:" + payTransferDay);
		
	    //int dayOfMonth = Integer.parseInt(payTransferDay.getCmnDetailValue());
	    //LocalDate today = LocalDate.now();
	    //LocalDate payDate = LocalDate.of(today.getYear(), today.getMonth(), dayOfMonth);
	    
	    //System.out.println("today:"+today+" ,payDate:"+payDate);
	    
		return "success";
	}
	
	// 급여 대장 조회
	@GetMapping("/payroll")
	public String getPayroll(Model model) {
		List<PaymentDTO> payrolls = salaryService.getPayrolls();
		model.addAttribute("payrolls", payrolls);
		return "erp/salaries/payroll";
	}
	
	
	
	
	
	//==========================================
	
	@GetMapping("insertPayForm/{empId}")
	public String insertPayForm(@PathVariable("empId") String empId,PaymentDTO paymentDTO,Model model) {
		PaymentDTO payList = salaryService.getEmpPayment(empId);
		model.addAttribute("pay",payList);
		System.out.println("개인 지급내역>>>"+payList);
		
		return"erp/salaries/insertPayForm";
	}
	
	
	
	@PostMapping("/insertPay")
	@ResponseBody
	public String insertPay(Model model) {
		
		return"이체가 완료 되었습니다.";
	}
	
	
	//=====================================================
	//공통코드 DetailName 불러오는 메서드
	public static void commonCodeName(Model model , CommonService commonService) {
		
		List<String> groupCodes = commonService.getAllGroupCodes();
		System.out.println("그룹코드 리스트 :::::"+groupCodes);
//			String[] groupCodes = {"GENDER","DEPT","EDUCATION","EMP_STATUS","EMP_TYPE","MARITAL","PARKING","POSITION","USER_ROLE"};
		//공통코드 추가시 "NEW_CODE" 추가
		
		Map<String, List<CommonCodeDetailNameDTO>> commonCodeMap = new HashMap<>();
		
		for(String groupCode : groupCodes) {
			commonCodeMap.put(groupCode, commonService.getCodeListByGroup(groupCode));
		}
		model.addAttribute("commonCodeMap",commonCodeMap);
	}
	
}
