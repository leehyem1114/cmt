package com.example.cmtProject.controller.erp.salaries;


import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.example.cmtProject.dto.comm.CommonCodeDetailDTO;
import com.example.cmtProject.dto.comm.CommonCodeDetailNameDTO;
import com.example.cmtProject.dto.erp.employees.EmpListPreviewDTO;
import com.example.cmtProject.dto.erp.salaries.PayBasicDTO;
import com.example.cmtProject.dto.erp.salaries.PayEmpListDTO;
import com.example.cmtProject.dto.erp.salaries.PaySearchDTO;
import com.example.cmtProject.dto.erp.salaries.PaymentDTO;
import com.example.cmtProject.service.comm.CommonService;
import com.example.cmtProject.service.erp.employees.EmployeesService;
import com.example.cmtProject.service.erp.salaries.SalaryService;
import com.example.cmtProject.util.PdfGenerator;


@Controller
@RequestMapping("/salaries")
public class SalaryController {
	@Autowired
	private SalaryService salaryService;
	@Autowired
	private EmployeesService employeesService;
	@Autowired
	private CommonService commonService;
	@Autowired
	private SpringTemplateEngine templateEngine;
	
	public String parseThymeleafToHtml(Map<String, Object> data) {
	    Context context = new Context();
	    context.setVariables(data);
	    
	    String html = templateEngine.process("pdf/paySlip", context);
	    
	    return html;  // html 파일 경로
	}

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
		
		List<EmpListPreviewDTO> empList = employeesService.getEmpList();
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
		
		List<EmpListPreviewDTO> empList = employeesService.getEmpList();
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

	public String payTransfer(@RequestParam("position") String position, @RequestParam("empIdList") List<String> empIdList, Model model) {	
		
		//System.out.println("position:"+position+" ,empIdList:"+empIdList);
		
		//사원 정보
		List<PayEmpListDTO> payEmpList = salaryService.getEmpInfo(empIdList);
		
		// 급여 지급일 조회		
		String payday = salaryService.getPayDay();
		
	    //int dayOfMonth = Integer.parseInt(payday.getCmnDetailValue());
	    LocalDate today = LocalDate.now();
	    System.out.println("today:"+today.getDayOfMonth());
	    int todayInt = today.getDayOfMonth();
	    String todayStr = String.valueOf(todayInt);
	    
	    int year = today.getYear();
	    int month = today.getMonthValue();
	    System.out.println("year:"+year+" ,month:"+month);
	    
	    LocalDate date = LocalDate.of(year, month, 20); // 그달의 20일
	    
	    //지급일이 아닌 경우 바로 return
	    /*
	    if(!(payday.equals(todayStr) && !isHoliday(date))) {
	    	
	    	return "fail";
	    	
	    }*/
	    
		 // 직급별 기본급 가져오기
		 List<PayBasicDTO> payBasicList = salaryService.getPayBasic();
		 for(PayBasicDTO p : payBasicList) {
			 System.out.println(p.getEmpId());
			 System.out.println(p.getEmpName());
			 System.out.println(p.getPayBasic());
			 System.out.println(p.getPositionNo());
			 System.out.println(p.getPayNo());
			 
		 }
		 
		//보너스 
		List<CommonCodeDetailDTO> bonusList = commonService.getCommonCodeDetails("BONUS", null);
		List<CommonCodeDetailDTO> taxList = commonService.getCommonCodeDetails("TAX", null);
		
		System.out.println(bonusList.size());
		System.out.println(taxList.size());
		
		List<Map<String, BigDecimal>> evaluatedResult = new ArrayList<>();
		
		// 수식 평가 반복 => 수당 계산
		for(CommonCodeDetailDTO bonus : bonusList) {
			String expression = bonus.getCmnDetailValue2(); // 계산식
			
			System.out.println("expression:"+expression);
			
			String[] operandNames = expression.split("[+\\-\\*/]");
			System.out.println("추출한 피연산자 이름 목록 : " + Arrays.toString(operandNames));
			
			//List<Double> values = List.of(50.0, 20.0, 30.0); // 샘플 피연산자
			// List<Object> values = List.of(50.0, 20, 30.0);
			
			
			List<CommonCodeDetailDTO> values = commonService.getCommonCodeDetails("POSITION", null);
			
//			for(CommonCodeDetailDTO c : values) {
//				System.out.println(c.getCmnDetailValue());
//			}
			
			//System.out.println(values);
			
			// 수식 평가 수행할 JexlEngine 객체 생성
			JexlEngine jexl = new JexlBuilder().create();
			
			// 문자열 수식을 JexlExpression 객체를 통해 실제 식으로 변환
			JexlExpression jexlExpression = jexl.createExpression(expression); 
			System.out.println(jexlExpression.getSourceText()); //PAY_BASIC * 0.5
			
			// 수식에 사용될 피연산자를 관리하는 JexlContext 객체 생성
			JexlContext context = new MapContext();
			context.set("PAY_BASIC", 1000);
			System.out.println(context.get("PAY_BASIC"));
			
			
			// 연산식에 피연산자 대입하여 실제 연산 수행 후 Object 타입으로 결과값 리턴
			Object result = jexlExpression.evaluate(context);
			
			Map<String, BigDecimal> map = new HashMap<>();
			map.put("PAY_BONUS_HOLIDAY",(BigDecimal)result);
			
			evaluatedResult.add(map);
		}
	
		
	    	
	    
		return "success";
	}
	
	//공휴일인지 아닌지 판별하는 함수
	public boolean isHoliday(LocalDate date) {
	    DayOfWeek day = date.getDayOfWeek();
	    System.out.println("isHoliday day:" + day);
	    return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
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
	
	
	//pdf
	@GetMapping("/payPrint/{empId}")
	public String patPrint(@PathVariable("empId") String empId,PaymentDTO paymentDTO,Model model) throws Exception {
		PaymentDTO payList = salaryService.getEmpPayment(empId);
		
		Map<String, Object> data = new HashMap<>();
		data.put("pay", payList);
		data.put("today", LocalDate.now());

		String html = parseThymeleafToHtml(data); // 위에서 만든 메서드
		new PdfGenerator().generatePdf(html, "D:/pdfs/payslip.pdf");
		model.addAttribute("pay", payList); 
		System.out.println(">>>>>>>>>>>뿌려질 내용" + payList);
		
		return "pdf/paySlip";
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
