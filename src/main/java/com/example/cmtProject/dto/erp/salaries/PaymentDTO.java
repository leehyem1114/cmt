package com.example.cmtProject.dto.erp.salaries;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDTO {

    private Long payNo;               // 지급 번호
    private Long empNo;              // 사원 번호
    private String empName;			 // 사원명
    private String deptName;		 // 부서명
    private String deptPosition;     // 직급
    private String empType;          // 고용유형
    private LocalDate payDate;       // 지급일

    private Long payBasic;           // 기본급
    private Long payBonusOvertime;   // 야근수당
    private Long payBonusTech;       // 기술수당
    private Long payBonusLong;       // 근속수당
    private Long payBonusCommition;  // 성과급
    private Long payBonusHoliday;    // 명절수당

    private Long payBonusTotal;      // 총수당금액

    private Long payTaxPension;      // 국민연금
    private Long payTaxCare;         // 장기요양보험
    private Long payTaxHealth;       // 건강보험
    private Long payTaxEmployment;   // 고용보험
    private Long payTaxIncome;       // 소득세
    private Long payTaxResidence;    // 주민세

    private Long payTaxTotal;        // 총공제금액

    private Long payTotal;           // 실수령액

    private String payStatus;        // 지급 상태 (미발급 / 발급완료 등)
}
