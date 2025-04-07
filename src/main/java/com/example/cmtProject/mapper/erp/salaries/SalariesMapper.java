package com.example.cmtProject.mapper.erp.salaries;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.cmtProject.dto.erp.employees.EmpListPreviewDTO;
import com.example.cmtProject.dto.erp.salaries.PayBasicDTO;
import com.example.cmtProject.dto.erp.salaries.PayCmmCodeDetailDTO;
import com.example.cmtProject.dto.erp.salaries.PayEmpListDTO;
import com.example.cmtProject.dto.erp.salaries.PaySearchDTO;
import com.example.cmtProject.dto.erp.salaries.PaymentDTO;

@Mapper
public interface SalariesMapper {

	// List<SalaryItemDTO> salItemTypes();
	
	// 급여 지급 내역
	List<PaymentDTO> getPayList();
	
	// 급여 지급 내역 필터링 검색
	List<PaymentDTO> searchPayList(PaySearchDTO paySearchDTO);
	
	// 야근 수당 계산
	List<PaymentDTO> getOverTimes(PaymentDTO paymentDTO);
	
	//개인 지급내역
	PaymentDTO selectEmpPayment(String empId);

	// 직급별 기본급
	List<PayBasicDTO> getPayBasic();

	// 급여 이체
	void savePayment(PaymentDTO paymentDTO);

	// 사원 정보
	List<PayEmpListDTO> getEmpInfo(@Param("empNoList") List<String> empNoList);

	// 급여 지급일
	String getPayDay();

	//공통 코드에서 수당, 공제 계산 하기위한 컬럼 가져오기
	List<PayCmmCodeDetailDTO> getPayCommonCodeDetails();

	List<EmpListPreviewDTO> getEmpList();
	
}
