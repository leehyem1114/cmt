package com.example.cmtProject.mapper.erp.salaries;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.cmtProject.dto.erp.employees.EmpListPreviewDTO;
import com.example.cmtProject.dto.erp.salaries.PayBasicDTO;
import com.example.cmtProject.dto.erp.salaries.PayCmmCodeDetailDTO;
import com.example.cmtProject.dto.erp.salaries.PayEmpListDTO;
import com.example.cmtProject.dto.erp.salaries.PaySearchDTO;
import com.example.cmtProject.dto.erp.salaries.PaymentDTO;
import com.example.cmtProject.dto.erp.salaries.PaymentTempDTO;

@Mapper
public interface SalariesMapper {

	// List<SalaryItemDTO> salItemTypes();
	
	// 급여 지급 내역
	List<PaymentDTO> getPayList(@Param("empId") String empId);
	
	// 급여 지급 내역 필터링 검색
	List<PaySearchDTO> getSearchPayList(PaySearchDTO paySearchDTO);
	
	// 야근 수당 계산
	List<PaymentDTO> getOverTimes(PaymentDTO paymentDTO);
	
	//개인 지급내역
	PaymentDTO selectEmpPayment(String empId);

	// 직급별 기본급
	List<PayBasicDTO> getPayBasic();

	// 급여 이체
	int savePayment(List<Map<String, Object>> evaluatedResult);

	// 사원 정보
	List<PayEmpListDTO> getEmpInfo(@Param("empNoList") List<String> empNoList);

	// 급여 지급일
	String getPayDay();

	//공통 코드에서 수당, 공제 계산 하기위한 컬럼 가져오기
	List<PayCmmCodeDetailDTO> getPayCommonCodeDetails();

	List<EmpListPreviewDTO> getEmpList();

	Long getNextPayNo();

	void savePaymentMap(Map<String, Object> m);

	void savePaymentDto(PaymentTempDTO pdto);
	
	// 월별 급여 대장 간략 조회
	List<PaymentDTO> getMonthlyPayrollSummaryList();
	
	// 월별 급여 대장 상세 조회 
	List<PaymentDTO> getMonthlyPayrollDetailList(@Param("payMonth") String payMonth);

	// 월별 급여 대장 - 급여 현황
	List<PaymentDTO> getMonthlyDeptPayrollList(String payMonth);

	// 월별 급여 대장 - 전 직원 급여 합계
	Map<String, Object> getMonthlyPayrollTotalList(String payMonth);

	// 연간 급여 대장
	List<Map<String, Object>> selectYearlyPayrollList(String payYear);

	// 연간 급여 대장 - 연도 가져오기
	List<Integer> getYears();
	
}
