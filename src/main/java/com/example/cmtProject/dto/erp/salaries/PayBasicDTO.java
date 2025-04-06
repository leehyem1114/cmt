package com.example.cmtProject.dto.erp.salaries;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PayBasicDTO {
	
	//private Long payNo;
	private String empId; // 사원번호
	private String empName; // 사원명
	private Long positionNo; // 직급번호
	private Long payBasic; // 기본급
}
