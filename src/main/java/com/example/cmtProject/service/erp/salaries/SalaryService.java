package com.example.cmtProject.service.erp.salaries;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.cmtProject.dto.erp.salaries.PayBasicDTO;
import com.example.cmtProject.dto.erp.salaries.PayCmmCodeDetailDTO;
import com.example.cmtProject.dto.erp.salaries.PayEmpListDTO;
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
	public List<PaymentDTO> getSearchPayList(PaySearchDTO paySearchDTO) {
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

	// 직급별 기본급 계산
	public List<PayBasicDTO> getPayBasic() {
		return salMapper.getPayBasic();
	}

	// 급여 이체
	public void savePayment(PaymentDTO paymentDTO) {

//		PaymentDTO paymentTransferDTO = PaymentDTO.builder()
//				.empNo(paymentDTO.getEmpNo())
//		        .empName(paymentDTO.getEmpName())
//		        .deptName(paymentDTO.getDeptName())
//		        .position(paymentDTO.getPosition())
//		        .empType(paymentDTO.getEmpType())
//		        .payDate(paymentDTO.getPayDate())
//		        .payBasic(paymentDTO.getPayBasic())
//		        .payBonusOvertime(paymentDTO.getPayBonusOvertime())
//		        .payBonusHoliday(paymentDTO.getPayBonusHoliday())
//		        .payBonusTotal(paymentDTO.getPayBonusTotal())
//		        .payTaxPension(paymentDTO.getPayTaxPension())
//		        .payTaxCare(paymentDTO.getPayTaxCare())
//		        .payTaxHealth(paymentDTO.getPayTaxHealth())
//		        .payTaxEmployment(paymentDTO.getPayTaxEmployment())
//		        .payTaxIncome(paymentDTO.getPayTaxIncome())
//		        .payTaxResidence(paymentDTO.getPayTaxResidence())
//		        .payTaxTotal(paymentDTO.getPayTaxTotal())
//		        .payTotal(paymentDTO.getPayTotal())
//		        .payStatus(paymentDTO.getPayStatus())
//		        .salBankName(paymentDTO.getSalBankName())
//		        .salBankAccount(paymentDTO.getSalBankAccount())
//		        .build();
		
		salMapper.savePayment(paymentDTO);
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

}
