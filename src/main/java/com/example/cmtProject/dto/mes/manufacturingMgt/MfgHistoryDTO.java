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
public class MfgHistoryDTO { // 생산 이력 DTO

    private String mpCode;			// 생산 계획 코드
    private String mpStatus;        // 생산 계획 상태
    private String itemType;		// 상품 구분
    private String pdtName;			// 상품명
    private String woNo;    		// 작업 지시 번호
    private String prcName;			// 공정명
    private String lineName;		// 라인명
    private String fqcInspectionStatus;	// 출고 검사 상태
    private LocalDate woStartDate;  // 생산 시작일
    private LocalDate woEndDate;    // 생산 종료일
    
}
