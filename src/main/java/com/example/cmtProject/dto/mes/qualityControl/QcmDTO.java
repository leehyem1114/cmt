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
    private String mtlName;           // 원자재 Name (외래키)
    private String pdtName;           // 완제품 Name (외래키)
    private Double qcmTargetValue;  // 목표값
    private Double qcmMaxValue;     // 상한값
    private Double qcmMinValue;     // 하한값
    private String qcmUnitWeight;   // 단위 (ex: g)
    private String qcmUnitLength;   // 단위 (ex: mm)
    private String qcmMethod;       // 검사방법

}
