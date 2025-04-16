package com.example.cmtProject.dto.mes.qualityControl;

import java.time.LocalDateTime;

import groovy.transform.builder.Builder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IqcDTO {
	
	private Long iqcNo;                 // 입고 검사 NO
    private String iqcCode;            // 입고 검사 코드
    private String empId;              // 사용자 ID
    private Long qcmNo;               // 입고 검사 기준 번호
    private Long mtlNo;                // 원자재 NO
    private LocalDateTime iqcTime;     // 검사 시간
    private Double iqcMeasuredValue;   // 측정값
    private String iqcInspectionStatus; // 검사 상태 (검사전/검사중/검사완료)
    private String iqcInspectionResult; // 합격 여부 (P/F)
    private Long whsNo;                // 입고될 창고
    private String lotNo;              // LOT번호

}
