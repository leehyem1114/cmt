package com.example.cmtProject.dto.erp.salaries;

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
public class PayBasicDTO {
	
	private Long payNo;
	private String empId;
	private String empName;
	private Long positionNo;
	private Long payBasic;
}
