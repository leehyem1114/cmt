package com.example.cmtProject.dto.erp.salaries;

import com.example.cmtProject.entity.Salary;
import com.example.cmtProject.entity.SalaryDetail;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class SalaryDetailDTO {
    private Long salDetailNo;     // 급여상세번호
    private Long salNo;           // 급여번호
    private Long salItemNo;       // 급여항목번호
    private Integer salItemAmount; // 급여 항목 금액
    
    @Builder
	public SalaryDetailDTO(Long salDetailNo, Long salNo, Long salItemNo, Integer salItemAmount) {
		this.salDetailNo = salDetailNo;
		this.salNo = salNo;
		this.salItemNo = salItemNo;
		this.salItemAmount = salItemAmount;
	}
    
	// SalaryDetailDTO -> SalaryDetail(엔티티) 로 변환하는 toEntity() 메서드 정의
	public SalaryDetail toEntity() {
	    return SalaryDetail.builder()
	    		.salDetailNo(this.salDetailNo)
	    		.salNo(this.salNo)
	    		.salItemNo(this.salItemNo)
	    		.salItemAmount(this.salItemAmount)
	            .build();
	}
    
}
