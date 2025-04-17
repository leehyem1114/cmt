package com.example.cmtProject.entity.mes.manufacturingMgt;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "MFG_SCHEDULES")
@NoArgsConstructor
@AllArgsConstructor
public class MfgSchedule { // 제조 계획

	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_MFG_SCHEDULES_MS_NO")
    @SequenceGenerator(name = "SEQ_MFG_SCHEDULES_MS_NO", sequenceName = "SEQ_MFG_SCHEDULES_MS_NO")
    @Column(name = "MS_NO")
	private Long msNo;  // 제조 계획 번호
	
    @Column(name = "MP_NO")
	private Long mpNo;  // 생산 계획 번호
    
    @Column(name = "PDT_CODE")
	private String pdtCode;  // 제품 코드

    @Column(name = "PRC_CODE")
	private String prcCode;  // 공정 코드

    @Column(name = "EMP_ID")
	private String empId;  // 등록 직원 사번

    @Column(name = "ALLOCATED_QTY")
	private Long allocatedQty;  // 계획 수량

    @Column(name = "MS_STATUS")
	private String msStatus;  // 제조 계획 상태

    @Column(name = "MS_PRIORITY")
	private String msPriority;  // 우선순위

    @Column(name = "MS_CREATED_AT")
	private LocalDate msCreatedAt;  // 등록일자
    
    @Column(name = "MS_UPDATED_AT")
	private LocalDate msUpdatedAt;  // 수정일자

    @Column(name = "MS_START_DATE")
	private LocalDate msStartDate;  // 제조 시작 예정일
    
    @Column(name = "MS_END_DATE")
	private LocalDate msEndDate;  // 제조 종료 예정일
    
}
