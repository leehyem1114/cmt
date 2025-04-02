package com.example.cmtProject.service.erp.salaries;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.cmtProject.dto.erp.salaries.PaySearchDTO;
import com.example.cmtProject.dto.erp.salaries.PaymentDTO;
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
		return salMapper.searchPayList(paySearchDTO);
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

}
