package com.example.cmtProject.dto.erp.salaries;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PaymentDTO {  

    private Long payNo;              // 지급 번호
    private Long empNo;              // 사원 NO
    private String empName;			 // 사원명
    private String empId;			 // 사원번호
    private String deptName;		 // 부서명
    private String position;     	 // 직급
    private String empType;          // 고용유형
    private LocalDate payDate;       // 지급일

    private Long payBasic;           // 기본급
    private BigDecimal payBonusOvertime;   // 야근수당
    private BigDecimal payBonusHoliday;    // 명절수당

    private Long payBonusTotal;      // 총수당금액

    private BigDecimal payTaxPension;      // 국민연금
    private BigDecimal payTaxCare;         // 장기요양보험
    private BigDecimal payTaxHealth;       // 건강보험
    private BigDecimal payTaxEmployment;   // 고용보험
    private BigDecimal payTaxIncome;       // 소득세
    private BigDecimal payTaxResidence;    // 주민세

    private Long payTaxTotal;        // 총공제금액

    private Long payTotal;           // 실수령액

    private String payStatus;        // 지급 상태 (미발급 / 발급완료 등)
    
    private String salBankName;	     // 은행명
    private String salBankAccount;	 // 계좌번호
    
}
