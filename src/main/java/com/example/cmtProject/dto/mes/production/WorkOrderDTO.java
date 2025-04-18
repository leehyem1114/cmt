package com.example.cmtProject.dto.mes.production;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkOrderDTO {

    private Long workOrderNo;           // 작업지시 번호
    private String workOrderCode;       // 작업지시 코드
    private String pdtCode;             // 제품 코드
    private String pdtName;             // 제품 이름
    private LocalDate orderDate;        // 지시 날짜
    private String orderQty;            // 지시 수량
    private String status;              // 진행 상태
    private LocalDate dueDate;          // 납기일
    private String comments;            // 비고
    private String useYn;               // 사용 여부
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime workStartDate; // 작업 시작 시간
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime workEndDate;   // 작업 종료 시간

}
