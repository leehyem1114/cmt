package com.example.cmtProject.dto.mes.qualityControl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IqcmDTO {
	
	private Long iqcmNo;             // 입고 검사 기준 NO
    private String iqcmCode;         // 입고 검사 기준 코드
    private String iqcmName;         // 입고 검사 이름
    private Long mtlNo;              // 원자재 NO (외래키)
    private Double iqcmTargetValue;  // 목표값
    private Double iqcmMaxValue;     // 상한값
    private Double iqcmMinValue;     // 하한값
    private String iqcmUnit;         // 단위 (ex: mm, g)
    private String iqcmMethod;       // 검사방법

}
