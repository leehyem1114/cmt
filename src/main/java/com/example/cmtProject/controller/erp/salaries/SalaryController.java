package com.example.cmtProject.controller.erp.salaries;


import java.time.LocalDate;
import java.security.PublicKey;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
		
		
		System.out.println("payList:"+payList);
		
		// 공통 코드에서 가져오기
		List<CommonCodeDetailNameDTO> deptList = commonService.getCodeListByGroup("DEPT");
		model.addAttribute("deptList", deptList);
		
		List<EmpListPreviewDTO> empList = employeesService.getEmplist();
		model.addAttribute("empList", empList);
		System.out.println("사원 목록 확인 : " + empList);
		
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
		// 사원 목록 모달창 출력 
		List<EmpListPreviewDTO> empList = employeesService.getEmplist();
		model.addAttribute("empList", empList);
		
		// 급여 지급일 조회
		CommonCodeDetailDTO payTransferDay = commonService.getCommonCodeDetail("PAYDAY", null);
	    int dayOfMonth = Integer.parseInt(payTransferDay.getCmnDetailValue());
	    LocalDate today = LocalDate.now();
	    LocalDate payDate = LocalDate.of(today.getYear(), today.getMonth(), dayOfMonth);
	    
	    // 공통 코드에서 가져오기
		List<CommonCodeDetailDTO> positionList = commonService.getCommonCodeDetails("POSITION", null);
		model.addAttribute("positionList", positionList);
		model.addAttribute("payBasic", positionList);
		List<CommonCodeDetailDTO> bonusList = commonService.getCommonCodeDetails("BONUS", null);
		List<CommonCodeDetailDTO> taxList = commonService.getCommonCodeDetails("TAX", null);
	
		System.out.println("공통코드 확인 : " + bonusList);
		System.out.println("공통코드 확인 : " + taxList);
		
		// 직급별 기본급 가져오기
		List<PayPositionDTO> payPositionList = salaryService.getPayAndPosition();

		// 기본급 맵 구성
		Map<String, Integer> payBasicMap = payPositionList.stream()
		    .collect(Collectors.toMap(
		    	payPosition -> payPosition.getCmnDetailName(), // 직급
		    	payPosition -> Integer.parseInt(payPosition.getCmnDetailValue()) // 문자 -> 숫자
		    ));

		// 선택한 직급의 기본급
		Integer payBasic = payBasicMap.getOrDefault(position, 0);

		Map<String, Object> operandMap = new HashMap<>();
		
		// 공통코드 값들 넣기
		for (CommonCodeDetailDTO pos : positionList) {
			operandMap.put(pos.getCmnDetailCode(), Double.parseDouble(pos.getCmnDetailValue()));
		}
		
		operandMap.put("PAY_BASIC", payBasic.doubleValue());
	
		// 결과 저장 리스트 선언
		List<Map<String, Double>> payTransferList = new ArrayList<>();
		
		// 기본급 결과 저장
		Map<String, Double> basicResult = new HashMap<>();
		basicResult.put("PAY_BASIC", payBasic.doubleValue());
		payTransferList.add(basicResult);

		// 수식 평가 반복 => 수당 계산
		Map<String, Double> bonusResult = new HashMap<>();
		for(CommonCodeDetailDTO bonus : bonusList) {
			String expression = bonus.getCmnDetailValue2(); // 계산식
			
			String[] operandNames = expression.split("[+\\-\\*/]");
			System.out.println("추출한 피연산자 이름 목록 : " + Arrays.toString(operandNames));
			
			// List<Double> values = List.of(50.0, 20.0, 30.0); // 샘플 피연산자
			// List<Object> values = List.of(50.0, 20, 30.0);
		//	List<CommonCodeDetailDTO> values = commonService.getCommonCodeDetails("POSITION", null);
			// System.out.println("조회한 피연산자 데이터 목록 : " + values);
			
			// 수식 평가 수행할 JexlEngine 객체 생성
			JexlEngine jexl = new JexlBuilder().create();

			// 문자열 수식을 JexlExpression 객체를 통해 실제 식으로 변환
			JexlExpression jexlExpression = jexl.createExpression(expression); 
			
			// 수식에 사용될 피연산자를 관리하는 JexlContext 객체 생성
			JexlContext context = new MapContext();

			// 선택된 직급의 기본급 포함한 모든 피연산자 값 context에 주입
		    for (String operand : operandNames) {
		        String key = operand.trim();
		        Object value = operandMap.getOrDefault(key, 0.0); // 기본값 처리
		        context.set(key, value);
		    }

			// 연산식에 피연산자 대입하여 실제 연산 수행 후 Object 타입으로 결과값 리턴
			Object result = jexlExpression.evaluate(context);
			
			// 결과값 Double 로 변환
			double val = Double.parseDouble(result.toString());
			
			// 계산된 수당 항목 payTransferList에 추가
	        payTransferList.add(Map.of(bonus.getCmnDetailCode(), val));
	        
	        bonusResult.put(bonus.getCmnDetailCode(), val);
		}	
		
		// 수식 평가 반복 => 공제 계산
		Map<String, Double> taxResult = new HashMap<>();
		for (CommonCodeDetailDTO tax : taxList) {
			String expression = tax.getCmnDetailValue2(); // 계산식

			String[] operandNames = expression.split("[+\\-\\*/]");
			System.out.println("추출한 피연산자 이름 목록 : " + Arrays.toString(operandNames));

			//List<Double> values = List.of(50.0, 20.0, 30.0); // 샘플 피연산자
			// List<Object> values = List.of(50.0, 20, 30.0);
			//System.out.println("조회한 피연산자 데이터 목록 : " + values);

			// 수식 평가 수행할 JexlEngine 객체 생성
			JexlEngine jexl = new JexlBuilder().create();

			// 문자열 수식을 JexlExpression 객체를 통해 실제 식으로 변환
			JexlExpression jexlExpression = jexl.createExpression(expression);

			// 수식에 사용될 피연산자를 관리하는 JexlContext 객체 생성
			JexlContext context = new MapContext();

		    // operandMap에서 피연산자 값을 context에 주입
		    for (String operand : operandNames) {
		        String key = operand.trim();
		        Object value = operandMap.getOrDefault(key, 0.0); // 기본값 처리
		        context.set(key, value);
		    }
			
			// 연산식에 피연산자 대입하여 실제 연산 수행 후 Object 타입으로 결과값 리턴
			Object result = jexlExpression.evaluate(context);
			
			// 결과값 Double 로 변환
			double val = Double.parseDouble(result.toString());
			
			// 계산된 수당 항목 payTransferList에 추가
	        payTransferList.add(Map.of(tax.getCmnDetailCode(), val));
	        
	        taxResult.put(tax.getCmnDetailCode(), val);
		}
	
		double bonusTotal = bonusResult.values().stream().mapToDouble(doubleValue -> doubleValue.doubleValue()).sum(); // 총 수당액
		double taxTotal = taxResult.values().stream().mapToDouble(doubleValue -> doubleValue.doubleValue()).sum(); // 총 공제액
		long payTotal = Math.round(payBasic + bonusTotal - taxTotal); // 실 수령액
	    
	    // 전체 결과 출력
		System.out.println("실 지급액 : " + payTotal);
		
	    // 지급상태 값 공통코드에서 가져오기
		List<CommonCodeDetailDTO> payStatusList = commonService.getCommonCodeDetails("PAY_STATUS", null);

		CommonCodeDetailDTO payStatusCode = payStatusList.stream()
			    .filter(status -> "PYS001".equals(status.getCmnDetailCode()))  // "지급완료" 상태 값
			    .findFirst()
			    .orElse(null);  // 상태 값이 없으면 null 반환

		// "PYS001" 상태 코드명 없으면 기본값 "미지급" 사용
		String payStatus = (payStatusCode != null) ? payStatusCode.getCmnDetailCode() : "PYS001";  // 기본값

	    
		// 각 사원에 대한 PaymentDTO 생성 및 저장
/*	    for (String empNo : empNoList) {
	        PaymentDTO paymentDTO = PaymentDTO.builder()
	            .empNo(Long.parseLong(empNo))               // 사원 번호
	            .payDate(payDate)                           // 지급일
	            .payBasic(payBasic.longValue())             // 기본급	// 수당/공제별 값은 없는 상태임;
	            .payBonusTotal(Math.round(bonusTotal))      // 총 수당액
	            .payTaxTotal(Math.round(taxTotal))          // 총 공제액
	            .payTotal(payTotal)                         // 실지급액
	            .payStatus(payStatus)                     // 지급 상태
	            .build();*/

	        //salaryService.savePayment(paymentDTO); // 급여 정보 저장
	    //}
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
