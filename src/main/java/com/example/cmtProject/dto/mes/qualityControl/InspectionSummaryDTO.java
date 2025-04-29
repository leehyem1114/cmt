package com.example.cmtProject.dto.mes.qualityControl;

import lombok.Data;

@Data
public class InspectionSummaryDTO {
	
	private String iqcDate;
	private String fqcDate;
	private int passCount;
	private int inProgressCount;
	private int failCount;
	

}
