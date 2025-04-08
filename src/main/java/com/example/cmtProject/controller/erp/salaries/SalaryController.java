package com.example.cmtProject.controller.erp.salaries;


import java.math.BigDecimal;
import java.time.DayOfWeek;
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
import org.springframework.web.bind.annotation.ModelAttribute;
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
import com.example.cmtProject.dto.erp.salaries.PayCmmCodeDetailDTO;
import com.example.cmtProject.dto.erp.salaries.PayEmpListDTO;
import com.example.cmtProject.dto.erp.salaries.PaySearchDTO;
import com.example.cmtProject.dto.erp.salaries.PaymentDTO;
import com.example.cmtProject.dto.erp.salaries.PaymentTempDTO;
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
		
		System.out.println("payList:"+payList);
		
		// 공통 코드에서 가져오기
		List<CommonCodeDetailNameDTO> deptList = commonService.getCodeListByGroup("DEPT");
		model.addAttribute("deptList", deptList);
		
		//List<EmpListPreviewDTO> empList = employeesService.getEmpList();
		//model.addAttribute("empList", empList);
		List<EmpListPreviewDTO> empList = salaryService.getEmpList();
		model.addAttribute("empList", empList);
		System.out.println("사원 목록 확인 : " + empList);
		
		List<CommonCodeDetailDTO> payDay = commonService.getCommonCodeDetails("PAYDAY", null);
		model.addAttribute("payDay", payDay);
		
		return "erp/salaries/payList";
	}
	
	// 급여 지급 내역 검색 요청
	@PostMapping("/searchPayList")
	public String getSearchPay(@ModelAttribute PaySearchDTO paySearchDTO, Model model) {
		System.out.println("검색 대상 : " + paySearchDTO);
		
		//List<PaySearchDTO> paySearchList = salaryService.getSearchPayList(paySearchDTO);
//		model.addAttribute("paySearchList", paySearchList);
		//model.addAttribute("payList", paySearchList);
		
		model.addAttribute("paySearchDTO", paySearchDTO);
		
		List<CommonCodeDetailNameDTO> deptList = commonService.getCodeListByGroup("DEPT");
		model.addAttribute("deptList", deptList);
		
		List<EmpListPreviewDTO> empList = employeesService.getEmpList();
		model.addAttribute("empList", empList);
		
		List<PaySearchDTO> paySearchList = salaryService.getSearchPayList(paySearchDTO);
		
		
		
		return "erp/salaries/payList";
	}
	
	// 급여 이체
	@PostMapping("/payTransfer")
	@ResponseBody
	public String payTransfer(@RequestParam("position") String position, @RequestParam("empIdList") List<String> empIdList, Model model) {	
		
		// 급여 지급일 조회		
		String payday = salaryService.getPayDay();
		
	    //int dayOfMonth = Integer.parseInt(payday.getCmnDetailValue());
	    LocalDate today = LocalDate.now();
	    int todayInt = today.getDayOfMonth();
	    String todayStr = String.valueOf(todayInt);
	    
	    int year = today.getYear();
	    int month = today.getMonthValue();
	    
	    LocalDate date = LocalDate.of(year, month, 8); // 그달의 20일
	    
	    System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"+today);
	    
	    //지급일이 아닌 경우 바로 return
	    
	    if(!(payday.equals(todayStr) && !isHoliday(date))) {
	    	
	    	return "fail";
	    	
	    }
	    
		// 직급별 기본급 가져오기
		List<PayBasicDTO> payBasicList = salaryService.getPayBasic();
		
		
		//공통 코드에서 수당, 공제 계산 하기위한 컬럼 가져오기
		List<PayCmmCodeDetailDTO> payCommList = salaryService.getPayCommonCodeDetails();
		
		//연산 결과를 입력할 List
		List<Map<String, Object>> evaluatedResult = new ArrayList<>();
		
		//PAY_NO 중 가장 큰 PAY_NO 값 가져오기
		//Long maxPayNo = salaryService.getNextPayNo();
		//System.out.println("maxPayNo:"+maxPayNo);
		//사원 정보
		List<PayEmpListDTO> payEmpList = salaryService.getEmpInfo(empIdList);
		for(PayEmpListDTO p : payEmpList) {
			
			
			Map<String, Object> calcularatorMap = new HashMap<>();
			
			//PayNo 직접 입력 부분
//			++maxPayNo;
//			System.out.println("maxPayNo =====================:" + maxPayNo);
//			calcularatorMap.put("payNo",(long)(maxPayNo));
			
			calcularatorMap.put("deptName",  p.getDeptName());
			calcularatorMap.put("empId",  p.getEmpId());
			calcularatorMap.put("empName",  p.getEmpName());
			calcularatorMap.put("empType",  p.getEmpType());
			calcularatorMap.put("payBasic",  p.getPayBasic());
			calcularatorMap.put("salBankName",  p.getSalBankName());
			calcularatorMap.put("salBankAccount",  p.getSalBankAccount());
			calcularatorMap.put("position",  p.getPosition());
			//calcularatorMap.put("payDate",  p.getPayDate());
			
			//지급에 따라서 달라지는 기본급
			Long tempPayBasic = Long.valueOf(p.getPayBasic());
			
			//---------------------- 계산 시작 -----------------------------------------
			
			// 수식 평가 반복 => 수당, 공제 계산
			for(PayCmmCodeDetailDTO payComm : payCommList) {
				String expression = payComm.getCmnDetailValue2(); 
				String columnName = payComm.getCmnDetailValue(); 
				System.out.println("columnName:" + columnName);
				
				String[] operandNames =  expression.split("[+\\-\\*/\\(\\)]");
				
				// 수식 평가 수행할 JexlEngine 객체 생성
				JexlEngine jexl = new JexlBuilder().create();
				
				// 문자열 수식을 JexlExpression 객체를 통해 실제 식으로 변환
				JexlExpression jexlExpression = jexl.createExpression(expression); 
				
				// 수식에 사용될 피연산자를 관리하는 JexlContext 객체 생성
				JexlContext context = new MapContext();
				
				context.set("PAY_BASIC", tempPayBasic);
				
				// 연산식에 피연산자 대입하여 실제 연산 수행 후 Object 타입으로 결과값 리턴
				Object result = jexlExpression.evaluate(context);
				
				//calcularatorMap.put(columnName,BigDecimal.valueOf(result));
				if (result instanceof Number) {
				    // 2단계: Number로 캐스팅 후 double로 변환하여 BigDecimal로 래핑
				    BigDecimal value = BigDecimal.valueOf(((Number) result).doubleValue());
				    calcularatorMap.put(columnName, value);
				} else {
				    // 숫자가 아니면 null 처리하거나 예외 처리
				    calcularatorMap.put(columnName, null);  // 또는 throw new IllegalArgumentException(...)
				}
				//System.out.println("----- calcularatorMap 확인 : " + calcularatorMap);
			}
			
			evaluatedResult.add(calcularatorMap);
			System.out.println("=============계산끝" + calcularatorMap);
			
			//---------------------- 계산 끝 -----------------------------------------
		}
		
		
		
		for(Map<String, Object> m : evaluatedResult) {
			//System.out.println(" m.get(\"payBonusOvertime\") 확인---------------" + m.get("PAY_BONUS_HOLIDAY"));
			System.out.println(" m 확인---------------" + m);
			
			PaymentDTO pdto = new PaymentDTO();
			//pdto.setPayBonusHoliday((BigDecimal) m.get("payBonusHoliday"));
			//PaymentTempDTO pdto = new PaymentTempDTO();
			//pdto.setPayBonusHoliday((BigDecimal) m.get("PAY_BONUS_HOLIDAY"));
			
			salaryService.savePaymentMap(m);
			//System.out.println("pdto.getPayBonusHoliday():"+pdto.getPayBonusHoliday());
			//salaryService.savePaymentDto(pdto);
			

			System.out.println("전달되는 map 값:");
			for (Map.Entry<String, Object> entry : m.entrySet()) {
			    System.out.println(entry.getKey() + " : " + entry.getValue());
			}
			
		}
		
		
		
		//System.out.println("evaluatedResult:"+evaluatedResult.toString());
		
		//int calculatorResult = salaryService.savePayment(evaluatedResult);
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
