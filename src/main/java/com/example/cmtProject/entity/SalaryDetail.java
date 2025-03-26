package com.example.cmtProject.entity;

import java.time.LocalDate;

import com.example.cmtProject.dto.erp.salaries.SalaryDTO;
import com.example.cmtProject.dto.erp.salaries.SalaryDetailDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "SALARY_DETAILS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class SalaryDetail {

	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SAL_DETAIL_NO")
    private Long salDetailNo; // 급여상세번호

    @Column(name = "SAL_NO", nullable = false)
    private Long salNo; // 급여번호 (급여 테이블 참조, FK 없음)

    @Column(name = "SAL_ITEM_NO", nullable = false)
    private Long salItemNo; // 급여항목번호 (급여항목 테이블 참조, FK 없음)

    @Column(name = "SAL_ITEM_AMOUNT", nullable = false)
    private Integer salItemAmount; // 급여 항목 금액 (각 수당/공제에 대한 금액)
    
    public SalaryDetailDTO toDto() {
    	return SalaryDetailDTO.builder()
    			.salDetailNo(salDetailNo)
    			.salNo(salNo)
    			.salItemNo(salItemNo)
    			.salItemAmount(salItemAmount)
				.build();
    	
    }
}
