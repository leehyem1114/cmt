package com.example.cmtProject.dto.mes.production;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LotUpdateDTO {
	
	private String lotNo; 
	private String bomQty;
	private String childPdtCode;
	private String parentPdtCode;
	private String woCode;
	private String pdtCode;
}
