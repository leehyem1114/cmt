package com.example.cmtProject.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "SALARY_ITEMS")
@Getter
@Setter
@NoArgsConstructor
public class SalaryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SAL_ITEM_NO")
    private Long salItemNo; // 급여항목번호

    @Column(name = "SAL_ITEM_NAME", nullable = false, length = 50)
    private String salItemName; // 급여항목명 (야근수당, 국민연금 등)

    @Enumerated(EnumType.STRING)
    @Column(name = "SAL_ITEM_TYPE", nullable = false, length = 50)
    private SalaryItemType salItemType; // 급여유형 (BONUS: 수당, TAX: 공제)
}
