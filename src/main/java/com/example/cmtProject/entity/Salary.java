package com.example.cmtProject.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "SALARIES")
@Getter
@Setter
@NoArgsConstructor
public class Salary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SAL_NO")
    private Long salNo; // 급여번호

    @Column(name = "EMP_NO", nullable = false)
    private Long empNo; // 사원번호

    @Column(name = "SAL_SAVE_YEAR", nullable = false)
    private LocalDate salSaveYear; // 적용년도

    @Column(name = "SAL_UPDATE_DATE", nullable = false)
    private LocalDate salUpdateDate; // 수정일시

    @Column(name = "SALARY", nullable = false)
    private Integer salary; // 기본급

    @Column(name = "SAL_TOTAL_BONUS")
    private Integer salTotalBonus; // 총 수당 금액

    @Column(name = "SAL_TOTAL_TAX")
    private Integer salTotalTax; // 총 공제 금액

    @Column(name = "SAL_NET_PAY", nullable = false)
    private Integer salNetPay; // 실수령액 (기본급 + 각종 수당 - 공제액)

    @Column(name = "SAL_BANK_NAME", nullable = false, length = 50)
    private String salBankName; // 은행명

    @Column(name = "SAL_BANK_ACCOUNT", nullable = false, length = 50)
    private String salBankAccount; // 계좌번호

    @Column(name = "SAL_DATE", nullable = false)
    private LocalDate salDate; // 급여지급일

    @Column(name = "SAL_STATE", nullable = false, length = 50)
    private String salState; // 급여지급상태 (미지급, 지급완료)
}