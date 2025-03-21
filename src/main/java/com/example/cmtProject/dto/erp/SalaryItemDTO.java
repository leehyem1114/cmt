package com.example.cmtProject.dto.erp;

import java.time.LocalDate;

import com.example.cmtProject.entity.SalaryItem;
import com.example.cmtProject.entity.SalaryItemType;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class SalaryItemDTO {
	private Long salItemNo; // 급여 유형 번호
	private SalaryItemType salItemType; // 급여 유형
	private String salItemName; // 급여 유형명
	private String salItemDesc; // 급여 유형 설명
	private String salItemCalc; // 계산식
	private Long salItemImportance; // 중요도
	private Long salItemApplyYear; // 적용년도
	private LocalDate salItemUpdate; // 최종수정일시
	
	@Builder
	public SalaryItemDTO(Long salItemNo, SalaryItemType salItemType, String salItemName, String salItemDesc, String salItemCalc, Long salItemImportance, Long salItemApplyYear, LocalDate salItemUpdate) {
		this.salItemNo = salItemNo;
		this.salItemType = salItemType;
		this.salItemName = salItemName;
		this.salItemDesc = salItemDesc;
		this.salItemCalc = salItemCalc;
		this.salItemImportance = salItemImportance;
		this.salItemApplyYear = salItemApplyYear;
		this.salItemUpdate = salItemUpdate;
	}
	
	// SalaryItemDTO -> SalaryItem(엔티티) 로 변환하는 toEntity() 메서드 정의
//	public SalaryItem toEntity() {
//	    return SalaryItem.builder()
//				.salItemNo(this.salItemNo)
//	            .salItemType(this.salItemType)
//	            .salItemName(this.salItemName)
//	            .salItemDesc(this.salItemDesc)
//	            .salItemCalc(this.salItemCalc)
//	            .salItemImportance(this.salItemImportance)
//	            .salItemApplyYear(this.salItemApplyYear)
//	            .salItemUpdate(this.salItemUpdate)
//	            .build();
//	}
	
}
