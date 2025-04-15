package com.example.cmtProject.dto.mes.qualityControl;

import groovy.transform.builder.Builder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FqcmDTO {
	
	private Long fqcmNo;              // 출고 검사 기준 NO
    private String fqcmCode;          // 출고 검사 기준 코드
    private String fqcmName;          // 출고 검사 이름
    private Long pdtNo;               // 완제품 NO (외래키)
    private Double fqcmTargetValue;   // 목표값
    private Double fqcmMaxValue;      // 상한값
    private Double fqcmMinValue;      // 하한값
    private String fqcmUnit;          // 단위 (mm, ml, g, 등)
    private String fqcmMethod;        // 검사 방법

}
