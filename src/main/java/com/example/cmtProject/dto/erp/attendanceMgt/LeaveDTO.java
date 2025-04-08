package com.example.cmtProject.dto.erp.attendanceMgt;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
public class LeaveDTO {
	
	private Long levNo; // 휴가NO
	private String empId; // 사원번호
	private String levType; // 휴가유형
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDateTime levStartDate; // 휴가 시작일
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDateTime levEndDate; // 휴가 종료일
	private int levDays; // 휴가일수
	private int levLeftDays; // 남은 휴가일수
	private String levReason; // 사유
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDateTime levReqDate; // 신청일시
	private String levApprovalStatus; // 승인상태
	private Long levApprover; // 승인자 empNo
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDateTime levApprovalDate; // 승인일시
	private String levRemarks; // 비고 
	private String docId; // 문서 아이디(조인용)
	

    
    
    // join매핑을 위한 dto
    private String deptName; // 부서명 공통코드
    private String levTypeName; // 휴가 타입 공통코드
    private String empName; // 사원이름


    public LeaveDTO toEntity() {
    	return LeaveDTO.builder()
    			.levNo(levNo)
    			.empId(empId)
    			.levType(levType)
    			.levStartDate(levStartDate)
    			.levEndDate(levEndDate)
    			.levDays(levDays)
    			.levLeftDays(levLeftDays)
    			.levReason(levReason)
    			.levReqDate(levReqDate)
    			.levApprovalStatus(levApprovalStatus)
    			.levApprover(levApprover)
    			.levApprovalDate(levApprovalDate)
    			.levRemarks(levRemarks)
    			.deptName(deptName)
    			.levTypeName(levTypeName)
    			.empName(empName)
    			.build();
    }

    @Builder
	public LeaveDTO(String empId, String levType, LocalDateTime levStartDate, LocalDateTime levEndDate,
			int levDays, int levLeftDays, String levReason, LocalDateTime levReqDate, String levApprovalStatus, Long levApprover,
			LocalDateTime levApprovalDate, String levRemarks, String deptName, String levTypeName, String empName) {
    	this.empId = empId;
		this.levType = levType;
		this.levStartDate = levStartDate;
		this.levEndDate = levEndDate;
		this.levDays = levDays;
		this.levLeftDays = levLeftDays;
		this.levReason = levReason;
		this.levReqDate = levReqDate;
		this.levApprovalStatus = levApprovalStatus;
		this.levApprover = levApprover;
		this.levApprovalDate = levApprovalDate;
		this.levRemarks = levRemarks;
		this.deptName = deptName;
		this.levTypeName = levTypeName;
		this.empName = empName;
		
	}
    
    
    
    
    
    
    
    
    
}
