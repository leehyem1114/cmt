package com.example.cmtProject.dto.erp.salaries;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTempDTO {
	private Long payNo;              // 지급 번호
	private BigDecimal payBonusHoliday;    // 명절수당
}
