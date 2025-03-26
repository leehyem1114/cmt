package com.example.cmtProject.entity;

import java.time.LocalDate;

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
public class SalaryItem {

    @Id  @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SAL_ITEM_NO")
    private Long salItemNo; // 급여항목번호
    
    @Enumerated(EnumType.STRING)
    @Column(name = "SAL_ITEM_TYPE", nullable = false, length = 50)
    private SalaryItemType salItemType; // 급여유형 (BONUS: 수당, TAX: 공제)
    
    @Column(name = "SAL_ITEM_NAME", nullable = false, length = 50)
    private String salItemName; // 급여 유형명 (야근수당, 국민연금 등)
    
    @Column(name = "SAL_ITEM_DESC", nullable = false, length = 255)
    private String salItemDesc; // 급여 유형 설명
    
    @Column(name = "SAL_ITEM_CALC", nullable = false, length = 255)
    private String salItemCalc; // 계산식 (예: BASIC * 0.1 등)
    
    @Column(name = "SAL_ITEM_IMPORTANCE")
    private Long salItemImportance; // 중요도 (정렬 우선순위로도 활용 가능)
    
    @Column(name = "SAL_ITEM_APPLY_YEAR", nullable = false)
    private Long salItemApplyYear; // 적용 연도
    
    @Column(name = "SAL_ITEM_UPDATE_DATE", nullable = false)
    private LocalDate salItemUpdate; // 최종 수정일

    
    public SalaryItemDTO toDto() {
    	return SalaryItemDTO.builder()
				.salItemType(salItemType)
				.salItemName(salItemName)
				.salItemDesc(salItemDesc)
				.salItemCalc(salItemCalc)
				.salItemImportance(salItemImportance)
				.salItemApplyYear(salItemApplyYear)
				.salItemUpdate(salItemUpdate)
				.build();
    }

}
