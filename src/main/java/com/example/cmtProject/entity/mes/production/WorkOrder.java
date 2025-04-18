package com.example.cmtProject.entity.mes.production;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
@Table(name = "WORK_ORDER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkOrder {//작업지시

    @Id
    @Column(name = "WORK_ORDER_NO")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long workOrderNo; //작업지시 번호

    @Column(name = "WORK_ORDER_CODE", length = 100)
    private String workOrderCode; //작업지시 코드

    @Column(name = "PDT_CODE", length = 50)
    private String pdtCode; //제품코드

    @Column(name = "ORDER_DATE")
    private LocalDate orderDate; //지시날짜

    @Column(name = "ORDER_QTY", nullable = false)
    private String orderQty; //지시 수량

    @Column(name = "STATUS", length = 20)
    private String status; //진행 상태

    @Column(name = "DUE_DATE")
    private LocalDate dueDate; //납기일

    @Column(name = "COMMENTS", length = 500)
    private String comments; //비고

    @Column(name = "USE_YN", length = 1)
	    private String useYn; //사용여부
    
    @Column(name = "WORK_START_DATE")
    private LocalDateTime workStartDate; //작업 시작시간
    
    @Column(name = "WORK_END_DATE")
    private LocalDateTime WorkEndDate; //작업 종료시간
    

}
