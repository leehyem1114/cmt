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
	private String bomLevel;
	private String parentPdtCode;
	private String childItemCode;
	private String itemType;
	private String bomPrcType;
	private String msQty;
	private String comments;
	
}
