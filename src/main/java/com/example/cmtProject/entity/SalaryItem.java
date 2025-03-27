package com.example.cmtProject.entity;

import java.time.LocalDate;

import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.example.cmtProject.dto.erp.salaries.SalaryItemDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@EntityListeners(AuditingEntityListener.class)
@Table(name = "SALARY_ITEMS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class SalaryItem { // 급여 유형 관리 엔티티

    @Id
    @Column(name = "SLI_NO")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sliNo; // 급여 유형 번호
    
    @Column(name = "SLI_TYPE", nullable = false, length = 50)
    private String sliType; // 급여 유형
    
    @Column(name = "SLI_NAME", nullable = false, length = 50)
    private String sliName; // 급여 유형명
    
    @Column(name = "SLI_DESC", nullable = false, length = 255)
    private String sliDesc; // 급여 유형 설명
    
    @Column(name = "SLI_FORMULA", nullable = false, length = 255)
    private String sliFormula; // 계산식
    
    @Column(name = "SLI_PRIORITY", nullable = false, length = 50)
    private String sliPriority; // 우선순위
    
    @LastModifiedDate
    @Column(name = "SLI_UPDATE_AT")
    private LocalDate sliUpdateAt; // 최종수정일시
    
    
    public SalaryItemDTO toDto() {
    	return SalaryItemDTO.builder()
    			.sliNo(sliNo)
				.sliType(sliType)
				.sliName(sliName)
				.sliDesc(sliDesc)
				.sliFormula(sliFormula)
				.sliPriority(sliPriority)
				.sliUpdateAt(sliUpdateAt)
				.build();
    }

    // 급여 유형 수정을 위한 메서드
//	public void changeSalaryItem(SalaryItemDTO salaryItemDTO) {
//		this.sliNo = salaryItemDTO.getSliNo();
//		this.sliType = salaryItemDTO.getSalItemType();
//		this.sliName = salaryItemDTO.getSalItemName();
//		this.sliDesc = salaryItemDTO.getSalItemDesc();
//		this.sliFormula = salaryItemDTO.getSalItemCalc();
//		this.sliPriority = salaryItemDTO.getSalItemImportance();
//		
//	}

}
