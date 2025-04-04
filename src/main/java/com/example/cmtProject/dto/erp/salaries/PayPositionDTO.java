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
public class PayPositionDTO {
	 private Long payNo;
	 private Long empNo;
	 private String empName;
	 private Integer positionNo;
	 private String cmnDetailValue;
	 private String cmnDetailName;
}
