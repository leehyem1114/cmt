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


    private Long lotNo;
    private String lotCode;
    private String pdtCode;
    private String pdtName;
    private LocalDate createDate;
    private String prcType;
    private String lineCode;
    private String eqpCode;
    private String woCode;
    private String childLotCode;
    private String parentLotCode;
    private LocalDate startTime;
    private LocalDate finishTime;
    private String workOrderStatus;
    private String useYn;
    
    //------------------
    private Long woNo;           // 작업지시 번호
    private LocalDate orderDate;        // 지시 날짜
    private String woQty;            // 지시 수량
    private String status;              // 진행 상태
    private LocalDate dueDate;          // 납기일
    private String comments;            // 비고
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate woStartDate; // 작업 시작 시간
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate woEndDate;   // 작업 종료 시간
    
    //=============================
    private String msNo;
    private String msCode;
    private String prcCode;
    private String allocatedQty;
    private LocalDate msStartDate;
    private LocalDate msEndDate;
    private String empId;
    private String empName;
    private String msStatus;
    
 

	 	
}

