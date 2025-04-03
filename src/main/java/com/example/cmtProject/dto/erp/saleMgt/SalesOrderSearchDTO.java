package com.example.cmtProject.dto.erp.saleMgt;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesOrderSearchDTO {
	
	private String soCode;
	private Long soNo;
	private String pdtCode;
	private String cltCode;
	private String soStatus;
	private String dateType;
	private LocalDate startDate;
	private LocalDate endDate;
}
