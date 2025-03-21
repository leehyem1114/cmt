package com.example.cmtProject.dto.erp.employees;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class EmployeesDTO {
	private Long empNo; //사원번호(PK)
	private String empId;// 사원 ID = 사번
	private String empName; // 이름
}
