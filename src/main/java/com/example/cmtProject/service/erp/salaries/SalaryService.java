package com.example.cmtProject.service.erp.salaries;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.cmtProject.dto.erp.employees.EmpListPreviewDTO;
import com.example.cmtProject.dto.erp.salaries.PayBasicDTO;
import com.example.cmtProject.dto.erp.salaries.PayCmmCodeDetailDTO;
import com.example.cmtProject.dto.erp.salaries.PayEmpListDTO;
import com.example.cmtProject.dto.erp.salaries.PaySearchDTO;
import com.example.cmtProject.dto.erp.salaries.PaymentDTO;
import com.example.cmtProject.dto.erp.salaries.PaymentTempDTO;
import com.example.cmtProject.entity.erp.salaries.Payment;
import com.example.cmtProject.mapper.erp.salaries.SalariesMapper;
import com.example.cmtProject.repository.erp.salaries.SalaryRepository;

@Service
public class SalaryService {
	@Autowired
	private SalaryRepository salRepository;
	@Autowired
	private SalariesMapper salMapper;	
	
	// 급여 지급 내역 조회
	public List<PaymentDTO> getPayList() {
//		List<Payment> payList = salRepository.findAll();
//		return payList.stream()
//				.map(payment -> payment.toDto())
//				.collect(Collectors.toList());
		
		return salMapper.getPayList();
	}

	// 급여 지급 내역 조회 - 검색 기능 추가 
	public List<PaySearchDTO> getSearchPayList(PaySearchDTO paySearchDTO) {
		return salMapper.getSearchPayList(paySearchDTO);
	}
	
	// 야근 수당 계산
	public List<PaymentDTO> getOverTimes(PaymentDTO paymentDTO) {
		return salMapper.getOverTimes(paymentDTO);
	}
	
	// 급여 대장 조회
	public List<PaymentDTO> getPayrolls() {
		List<Payment> payrolls = salRepository.findAll();
		return payrolls.stream()
				.map(payment -> payment.toDto())
				.collect(Collectors.toList());
	}

	// 직급별 기본급 계산
	public List<PayBasicDTO> getPayBasic() {
		return salMapper.getPayBasic();
	}

	// 급여 이체
	public int savePayment(List<Map<String, Object>> evaluatedResult) {
		
		return salMapper.savePayment(evaluatedResult);
	}	

	//개인 지급내역
	public PaymentDTO getEmpPayment(String empId) {
		return salMapper.selectEmpPayment(empId);
	}

	public List<PayEmpListDTO> getEmpInfo(List<String> empNoList) {
		System.out.println("=============== service empNoList:"+empNoList);
		
		return salMapper.getEmpInfo(empNoList);
	}

	public String getPayDay() {
		
		return salMapper.getPayDay();
	}

	public List<PayCmmCodeDetailDTO> getPayCommonCodeDetails() {
		return salMapper.getPayCommonCodeDetails();
	}

	public List<EmpListPreviewDTO> getEmpList() {
		return salMapper.getEmpList();
	}
	
	public Long getNextPayNo() {
		return salMapper.getNextPayNo();
	}

	public void savePaymentMap(Map<String, Object> m) {
		salMapper.savePaymentMap(m);
	}

	public void savePaymentDto(PaymentTempDTO pdto) {
		salMapper.savePaymentDto(pdto);
	}



}
