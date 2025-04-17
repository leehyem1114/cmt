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
public class FqcDTO {
	
	private Long fqcNo;                // 출고 검사 NO
    private String fqcCode;           // 출고 검사 코드
    private String empId;             // 사용자 ID
    private String qcmName;              // 출고 검사 기준 번호
    private String pdtName;               // 완제품 NO
    private LocalDateTime fqcTime;    // 검사 시간
    private Double fqcMeasuredValue;  // 측정값
    private String qcmUnitWeight;   // 단위 (ex: g)
    private String qcmUnitLength;   // 단위 (ex: mm)
    private String fqcInspectionStatus; // 검사 상태 (검사전/검사중/검사완료)
    private String fqcInspectionResult; // 합격 여부 (P/F)
    private String whsName;               // 입고될 창고
    private String lotNo;             // LOT 번호

}
