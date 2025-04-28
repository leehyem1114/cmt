package com.example.cmtProject.dto.mes.manufacturingMgt;

import java.time.LocalDate;

import groovy.transform.builder.Builder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MfgScheduleDetailDTO {

	private Long msdNo;
	private String msCode;
	private String parentPdtCode;
	private String pdtName;
	private String itemType;
	private String pdtTypeName;
	private String msQty;
	private String qtyUnitName;
	private String comments;
	
}
