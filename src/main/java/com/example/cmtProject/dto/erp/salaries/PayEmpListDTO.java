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
	 private String empId;
	 private String empName;
	 private String empType;          // 고용유형
	 private String deptNo;
	 private String deptName;
//	 private Long positionNo;
	 private String position;
	 private String salBankName;	     // 은행명
	 private String salBankAccount;	 // 계좌번호
}
