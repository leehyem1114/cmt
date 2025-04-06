package com.example.cmtProject.entity.erp.salaries;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.cmtProject.dto.erp.salaries.PaymentDTO;

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

@Entity
@Table(name = "PAYMENTS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment { // 급여 지급 이력 엔티티

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PAY_NO")
    private Long payNo; // 지급 번호
    
    @Column(name = "EMP_NAME", nullable = false)
    private String empName;			 // 사원명
    
    @Column(name = "EMP_ID", nullable = false)
    private String empId; // 사원 번호

    @Column(name = "DEPT_NAME", nullable = false)
    private String deptName;		 // 부서명
    
    @Column(name = "POSITION", nullable = false)
    private String position;     	 // 직급
    
    @Column(name = "EMP_TYPE", nullable = false)
    private String empType;          // 고용유형
    
    @Column(name = "PAY_DATE", nullable = false)
    private LocalDate payDate; // 지급일

    @Column(name = "PAY_BASIC", nullable = false)
    private Long payBasic; // 기본급

    @Column(name = "PAY_BONUS_OVERTIME")
    private BigDecimal payBonusOvertime; // 야근수당

    @Column(name = "PAY_BONUS_HOLIDAY")
    private BigDecimal payBonusHoliday; // 명절수당

    @Column(name = "PAY_BONUS_TOTAL", nullable = false)
    private Long payBonusTotal; // 총수당금액

    @Column(name = "PAY_TAX_PENSION")
    private BigDecimal payTaxPension; // 국민연금

    @Column(name = "PAY_TAX_CARE")
    private BigDecimal payTaxCare; // 장기요양보험

    @Column(name = "PAY_TAX_HEALTH")
    private BigDecimal payTaxHealth; // 건강보험

    @Column(name = "PAY_TAX_EMPLOYMENT")
    private BigDecimal payTaxEmployment; // 고용보험

    @Column(name = "PAY_TAX_INCOME")
    private BigDecimal payTaxIncome; // 소득세

    @Column(name = "PAY_TAX_RESIDENCE")
    private BigDecimal payTaxResidence; // 주민세

    @Column(name = "PAY_TAX_TOTAL", nullable = false)
    private Long payTaxTotal; // 총공제금액

    @Column(name = "PAY_TOTAL", nullable = false)
    private Long payTotal; // 실수령액

    @Column(name = "PAY_STATUS", length = 50, nullable = false)
    private String payStatus;  // 지급 상태
    
    @Column(name = "SAL_BANK_NAME", length = 50, nullable = false)
    private String salBankName;  // 지급 상태
    
    @Column(name = "SAL_BANK_ACCOUNT", length = 50, nullable = false)
    private String salBankAccount;  // 지급 상태
    
    public PaymentDTO toDto() {
        return PaymentDTO.builder()
                .payNo(payNo)
                .empName(empName)
                .empId(empId)
                .deptName(deptName)
                .position(position)
                .empType(empType)
                .payDate(payDate)
                .payBasic(payBasic)
                .payBonusOvertime(payBonusOvertime)
                .payBonusHoliday(payBonusHoliday)
                .payBonusTotal(payBonusTotal)
                .payTaxPension(payTaxPension)
                .payTaxCare(payTaxCare)
                .payTaxHealth(payTaxHealth)
                .payTaxEmployment(payTaxEmployment)
                .payTaxIncome(payTaxIncome)
                .payTaxResidence(payTaxResidence)
                .payTaxTotal(payTaxTotal)
                .payTotal(payTotal)
                .payStatus(payStatus)
                .salBankName(salBankName)         // ✅ 누락된 필드
                .salBankAccount(salBankAccount)   // ✅ 누락된 필드
                .build();
    }

}
