package com.example.cmtProject.entity;

import java.time.LocalDate;

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
@Table(name = "PAYSLIPS")
@Getter
@Setter
@NoArgsConstructor
public class Payslip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PAY_NO")
    private Long payNo; // 명세서번호

    @Column(name = "EMP_NO", nullable = false)
    private Long empNo; // 사원번호

    @Column(name = "SAL_NO", nullable = false)
    private Long salNo; // 급여번호

    @Column(name = "PAY_DATE", nullable = false)
    private LocalDate payDate; // 발급일

    @Column(name = "PAY_BONUS_OVERTIME")
    private Integer payBonusOvertime; // 야근수당

    @Column(name = "PAY_BONUS_TECH")
    private Integer payBonusTech; // 기술수당

    @Column(name = "PAY_BONUS_LONG")
    private Integer payBonusLong; // 근속수당

    @Column(name = "PAY_BONUS_COMMITION")
    private Integer payBonusCommition; // 성과급

    @Column(name = "PAY_BONUS_HOLIDAY")
    private Integer payBonusHoliday; // 명절수당

    @Column(name = "PAY_BONUS_VACATION")
    private Integer payBonusVacation; // 휴가수당

    @Column(name = "PAY_TAX_PENSION")
    private Integer payTaxPension; // 국민연금

    @Column(name = "PAY_TAX_CARE")
    private Integer payTaxCare; // 장기요양보험

    @Column(name = "PAY_TAX_HEALTH")
    private Integer payTaxHealth; // 건강보험

    @Column(name = "PAY_TAX_EMPLOYMENT")
    private Integer payTaxEmployment; // 고용보험

    @Column(name = "PAY_TAX_INCOME")
    private Integer payTaxIncome; // 소득세

    @Column(name = "PAY_TAX_RESIDENCE")
    private Integer payTaxResidence; // 주민세

    @Enumerated(EnumType.STRING)
    @Column(name = "PAY_STATUS", nullable = false, length = 50)
    private PayslipStatus payStatus; // 발급상태 (미발급, 발급완료)
}

