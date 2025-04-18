package com.example.cmtProject.dto.mes.manufacturingMgt;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MfgPlanSalesOrderDTO {

	private String soCode; // 수주코드
	private String pdtCode; // 제품코드
	private String pdtName; // 제품명
	private Long soQuantity; // 주문량
	private String qtyUnit; // 주문단위
	private Long leadTime;  // 소요시간
	private String cltName; // 거래처명
	private LocalDate soDueDate; // 납품기한
	private LocalDate mpStartDate; // 생산 시작 예정일
	private LocalDate mpEndDate; // 생산 종료 예정일
	private String mpPriority; // 우선순위
}
