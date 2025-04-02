package com.example.cmtProject.dto.erp.salaries;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

//급여 지급 내역 화면에서 필터링 검색에 사용할 DTO 정의
@Getter
@Setter
@ToString
@NoArgsConstructor
public class PaySearchDTO {
	private String deptName; // 부서명
	private String empName; // 사원명
	
	private LocalDate minDate;
	private LocalDate maxDate;
}