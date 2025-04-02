package com.example.cmtProject.controller.erp.salaries;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.cmtProject.dto.comm.CommonCodeDetailDTO;
import com.example.cmtProject.dto.comm.CommonCodeDetailNameDTO;
import com.example.cmtProject.dto.erp.employees.EmpListPreviewDTO;
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
	public String getPayList(Model model) {
		commonCodeName(model,commonService);
		
		//List<PaymentDTO> payList = salaryService.getPayList();
		//model.addAttribute("payList", payList);
		model.addAttribute("paySearchDTO", new PaySearchDTO());
		
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
	public String payTransfer(@RequestParam("position") String position, Model model) {
		// 사원 목록 모달창 출력 
		List<EmpListPreviewDTO> empList = employeesService.getEmplist();
		model.addAttribute("empList", empList);
		
		// 결과 저장 리스트 선언
		List<Map<String, Double>> bonusResultList = new ArrayList<>();
		List<Map<String, Double>> taxResultList = new ArrayList<>();
		
		// 공통 코드에서 가져오기
		List<CommonCodeDetailDTO> positionList = commonService.getCommonCodeDetails("POSITION", null);
		model.addAttribute("positionList", positionList);
		List<CommonCodeDetailDTO> bonusList = commonService.getCommonCodeDetails("BONUS", null);
		List<CommonCodeDetailDTO> taxList = commonService.getCommonCodeDetails("TAX", null);
	
		System.out.println("공통코드 확인 : " + bonusList);
		System.out.println("공통코드 확인 : " + taxList);
		
		
		// 기본급 맵 구성
		Map<String, Integer> payBasicList = positionList.stream()
		    .collect(Collectors.toMap(
		        positionCode -> positionCode.getCmnDetailName(), // "대리", "과장"
		        positionCode -> Integer.parseInt(positionCode.getCmnDetailValue()) // 2000000 등
		    ));

		// 선택한 직급의 기본급
		Integer payBasic = payBasicList.getOrDefault(position, 0);

		// context에 기본급 저장
		Map<String, Object> operandContextMap = new HashMap<>();
		operandContextMap.put("PAY_BASIC", payBasic.doubleValue()); // 기본급은 PAY_BASIC 변수명으로 등록
		
		
		// 수식 평가 반복
		for(CommonCodeDetailDTO bonus : bonusList) {
			String expression = bonus.getCmnDetailValue2(); // 계산식
			
			String[] operandNames = expression.split("[+\\-\\*/]");
			System.out.println("추출한 피연산자 이름 목록 : " + Arrays.toString(operandNames));
			
			List<Double> values = List.of(50.0, 20.0, 30.0); // 샘플 피연산자
			// List<Object> values = List.of(50.0, 20, 30.0);\
			// System.out.println("조회한 피연산자 데이터 목록 : " + values);
			
			// 수식 평가 수행할 JexlEngine 객체 생성
			JexlEngine jexl = new JexlBuilder().create();

			// 문자열 수식을 JexlExpression 객체를 통해 실제 식으로 변환
			JexlExpression jexlExpression = jexl.createExpression(expression); 
			
			// 수식에 사용될 피연산자를 관리하는 JexlContext 객체 생성
			JexlContext context = new MapContext();
			
			// 피연산자 갯수만큼 반복하면서 JexlExpression 객체의 set() 메서드로 피연산자 대입
//			for(int i = 0; i < operandNames.length; i++) {
//				// 이 때, 피연산자명 앞뒤 공백 제거 위해 trim() 메서드 사용(ex. "a + b = c" 일 때 "a " 처럼 공백 포함됨)
//				context.set(operandNames[i].trim(), values.get(i));
//			}
			
			
		    // operandContextMap에 등록된 값들 모두 context에 넣기
		    for (Map.Entry<String, Object> entry : operandContextMap.entrySet()) {
		        context.set(entry.getKey(), entry.getValue());
		    }

		    // 수식에 필요한 나머지 피연산자들 체크
		    for (String operand : operandNames) {
		        String key = operand.trim();
		        if (!context.has(key)) {
		            context.set(key, 0.0); // 기본값 처리
		        }
		    }
			

			// 연산식에 피연산자 대입하여 실제 연산 수행 후 Object 타입으로 결과값 리턴
			Object result = jexlExpression.evaluate(context);
			
			// 결과 Map에 저장
	        Map<String, Double> resultMap = new HashMap<>();
	        resultMap.put(bonus.getCmnDetailCode(), Double.parseDouble(result.toString()));
	        bonusResultList.add(resultMap);
		}	
		
		// 수식 평가 반복
		for (CommonCodeDetailDTO tax : taxList) {
			String expression = tax.getCmnDetailValue2(); // 계산식

			String[] operandNames = expression.split("[+\\-\\*/]");
			System.out.println("추출한 피연산자 이름 목록 : " + Arrays.toString(operandNames));

			List<Double> values = List.of(50.0, 20.0, 30.0); // 샘플 피연산자
			// List<Object> values = List.of(50.0, 20, 30.0);
			System.out.println("조회한 피연산자 데이터 목록 : " + values);

			// 수식 평가 수행할 JexlEngine 객체 생성
			JexlEngine jexl = new JexlBuilder().create();

			// 문자열 수식을 JexlExpression 객체를 통해 실제 식으로 변환
			JexlExpression jexlExpression = jexl.createExpression(expression);

			// 수식에 사용될 피연산자를 관리하는 JexlContext 객체 생성
			JexlContext context = new MapContext();

//			// 피연산자 갯수만큼 반복하면서 JexlExpression 객체의 set() 메서드로 피연산자 대입
//			for (int i = 0; i < operandNames.length; i++) {
//				// 이 때, 피연산자명 앞뒤 공백 제거 위해 trim() 메서드 사용(ex. "a + b = c" 일 때 "a " 처럼 공백 포함됨)
//				context.set(operandNames[i].trim(), values.get(i));
//			}
			
			
		    // operandContextMap에 등록된 값들 모두 context에 넣기
		    for (Map.Entry<String, Object> entry : operandContextMap.entrySet()) {
		        context.set(entry.getKey(), entry.getValue());
		    }

		    // 수식에 필요한 나머지 피연산자들 체크
		    for (String operand : operandNames) {
		        String key = operand.trim();
		        if (!context.has(key)) {
		            context.set(key, 0.0); // 기본값 처리
		        }
		    }
			

			// 연산식에 피연산자 대입하여 실제 연산 수행 후 Object 타입으로 결과값 리턴
			Object result = jexlExpression.evaluate(context);

			// 결과 Map에 저장
			Map<String, Double> resultMap = new HashMap<>();
			resultMap.put(tax.getCmnDetailCode(), Double.parseDouble(result.toString()));
			taxResultList.add(resultMap);
		}
	
	        // 전체 결과 출력
	        System.out.println("수당 결과 리스트: " + bonusResultList);
	        System.out.println("공제 결과 리스트: " + taxResultList);
		
		
		return "erp/salaries/payList";
	}
	
	// 급여 대장 조회
	@GetMapping("/payroll")
	public String getPayroll(Model model) {
		List<PaymentDTO> payrolls = salaryService.getPayrolls();
		model.addAttribute("payrolls", payrolls);
		return "erp/salaries/payroll";
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
