package com.example.cmtProject.dto.mes.qualityControl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QcmDTO {
	
	private Long qcmNo;             // 입고 검사 기준 NO
    private String qcmCode;         // 입고 검사 기준 코드
    private String qcmName;         // 입고 검사 이름
    private Long mtlNo;              // 원자재 NO (외래키)
    private Long pdtNo;              // 완제품 NO (외래키)
    private Double qcmTargetValue;  // 목표값
    private Double qcmMaxValue;     // 상한값
    private Double qcmMinValue;     // 하한값
    private String qcmUnit;         // 단위 (ex: mm, g)
    private String qcmMethod;       // 검사방법

}
