package com.example.cmtProject.dto.erp.salaries;

import java.time.LocalDate;

import com.example.cmtProject.entity.SalaryItem;
import com.example.cmtProject.entity.erp.salaries.SalaryItemType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Builder
public class SalaryItemDTO {
	private Long sliNo; // 급여 유형 번호
	private SalaryItemType sliType; // 급여 유형
	private String sliName; // 급여 항목명
	private String sliDesc; // 급여 유형 설명
	private String sliFormula; // 계산식
	private String sliPriority; // 우선순위
	private LocalDate sliUpdateAt; // 최종수정일시

	@Builder
	public SalaryItemDTO(Long sliNo, SalaryItemType sliType, String sliName, String sliDesc, String sliFormula, String sliPriority, LocalDate sliUpdateAt) {
		this.sliNo = sliNo;
		this.sliType = sliType;
		this.sliName = sliName;
		this.sliDesc = sliDesc;
		this.sliFormula = sliFormula;
		this.sliPriority = sliPriority;
		this.sliUpdateAt = sliUpdateAt;
	}
	
	// SalaryItemDTO -> SalaryItem(엔티티) 로 변환하는 toEntity() 메서드 정의
	public SalaryItem toEntity() {
	    return SalaryItem.builder()
				.sliNo(this.sliNo)
	            .sliType(this.sliType)
	            .sliName(this.sliName)
	            .sliDesc(this.sliDesc)
	            .sliFormula(this.sliFormula)
	            .sliPriority(this.sliPriority)
	            .sliUpdateAt(this.sliUpdateAt)
	            .build();
	}
	
}
