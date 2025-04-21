package com.example.cmtProject.dto.mes.production;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LotDTO {

	 	private long lot_no;
	 	private String lotCode;  	// lot 코드
	 	private String pdtCode; 	//나의 제품 코드, PRODUCTS 테이블 조인
	 	private String childpdtcode;		// 이전 공정 자재
	    private LocalTime createDate;	// lot 생성일
	    private String prcType; 	// 공정 타입
	    private String lineCode;  	// 라인 코드
	    private String eqpCode;		// 설비 코드
	    private String woCode; // 작업지시서 번호
	    private String childLotCode; // 이전 공정 LOT
	    private LocalTime startTime; 	// 작업 시작 시간
	    private LocalTime finishTime;	// 작업 종료 시간
	    private String woStatusNo;	// 작업 상태(대기:STANDBY 진행중:RUNNING 종료:COMPLETED)
	    private String useYN;		// 사용 여부
}
