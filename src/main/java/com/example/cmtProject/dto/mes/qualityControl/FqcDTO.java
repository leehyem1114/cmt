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
    private Long qcmNo;              // 출고 검사 기준 번호
    private Long pdtNo;               // 완제품 NO
    private LocalDateTime fqcTime;    // 검사 시간
    private Double fqcMeasuredValue;  // 측정값
    private String fqcInspectionStatus; // 검사 상태 (검사전/검사중/검사완료)
    private String fqcInspectionResult; // 합격 여부 (P/F)
    private Long whsNo;               // 입고될 창고
    private String lotNo;             // LOT 번호

}
