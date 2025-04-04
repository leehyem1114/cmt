package com.example.cmtProject.mapper.erp.salaries;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.cmtProject.dto.erp.salaries.PayPositionDTO;
import com.example.cmtProject.dto.erp.salaries.PaySearchDTO;
import com.example.cmtProject.dto.erp.salaries.PaymentDTO;

@Mapper
public interface SalariesMapper {

	// List<SalaryItemDTO> salItemTypes();
	
	// 급여 지급 내역
	List<PaymentDTO> getPayList();
	
	// 급여 지급 내역 필터링 검색
	List<PaySearchDTO> searchPayList(PaySearchDTO paySearchDTO);
	
	// 야근 수당 계산
	List<PaymentDTO> getOverTimes(PaymentDTO paymentDTO);

	// 직급별 기본급
	List<PayPositionDTO> getPayAndPosition();

	// 급여 이체
	void savePayment(PaymentDTO paymentDTO);


}
