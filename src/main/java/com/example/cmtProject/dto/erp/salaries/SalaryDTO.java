package com.example.cmtProject.dto.erp.salaries;

import java.time.LocalDate;

import com.example.cmtProject.entity.Salary;
import com.example.cmtProject.entity.SalaryItem;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class SalaryDTO {
    private Long salNo; // 급여번호
    private Long empNo; // 사원번호
    private LocalDate salSaveYear; // 적용년도
    private LocalDate salUpdateDate; // 수정일시
    private Integer salary; // 기본급
    private Integer salTotalBonus; // 총 수당 금액
    private Integer salTotalTax; // 총 공제 금액
    private Integer salNetPay; // 실수령액
    private String salBankName; // 은행명
    private String salBankAccount; // 계좌번호
    private LocalDate salDate; // 급여지급일
    private String salState; // 급여지급상태 (미지급, 지급완료)
    
    @Builder
	public SalaryDTO(Long salNo, Long empNo, LocalDate salSaveYear, LocalDate salUpdateDate, Integer salary,
			Integer salTotalBonus, Integer salTotalTax, Integer salNetPay, String salBankName, String salBankAccount,
			LocalDate salDate, String salState) {
		this.salNo = salNo;
		this.empNo = empNo;
		this.salSaveYear = salSaveYear;
		this.salUpdateDate = salUpdateDate;
		this.salary = salary;
		this.salTotalBonus = salTotalBonus;
		this.salTotalTax = salTotalTax;
		this.salNetPay = salNetPay;
		this.salBankName = salBankName;
		this.salBankAccount = salBankAccount;
		this.salDate = salDate;
		this.salState = salState;
	}
    
	// SalaryDTO -> Salary(엔티티) 로 변환하는 toEntity() 메서드 정의
	public Salary toEntity() {
	    return Salary.builder()
	    		.salNo(this.salNo)
	            .empNo(this.empNo)
	            .salSaveYear(this.salSaveYear)
	            .salUpdateDate(this.salUpdateDate)
	            .salary(this.salary)
	            .salTotalBonus(this.salTotalBonus)
	            .salTotalTax(this.salTotalTax)
	            .salNetPay(this.salNetPay)
	            .salBankName(this.salBankName)
	            .salBankAccount(this.salBankAccount)
	            .salDate(this.salDate)
	            .salState(this.salState)
	            .build();
	}
    
    
}
