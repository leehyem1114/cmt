package com.example.cmtProject.dto.erp.salaries;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayEmpListDTO {
	 private String empId; // 사원번호
	 private String empName; // 사원명
	 private String empType;          // 고용유형
	 private Long deptNo; // 부서번호
	 private String deptName; // 부서명
//	 private Long positionNo; // 직급번호
	 private String position; // 직급명
	 private String salBankName;	     // 은행명
	 private String salBankAccount;	 // 계좌번호
}
