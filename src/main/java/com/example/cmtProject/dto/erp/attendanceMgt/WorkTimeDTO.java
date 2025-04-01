package com.example.cmtProject.dto.erp.attendanceMgt;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.example.cmtProject.entity.erp.attendanceMgt.WorkStatus;
import com.example.cmtProject.entity.erp.attendanceMgt.WorkTime;
import com.example.cmtProject.entity.erp.attendanceMgt.WorkType;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
public class WorkTimeDTO {
	
    private Long wktNo; // 근무시간NO
    private Long empNo; // 사원번호
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime wktDate; // 근무일자
    @JsonFormat(pattern = "HH:mm")
    private LocalDateTime wktStartTime; // 출근시간
    @JsonFormat(pattern = "HH:mm")
    private LocalDateTime wktEndTime; // 퇴근시간
    private String wktStatus; // 근무상태
    private String wktType; // 기준근무유형
    private String wktRemarks; // 비고
    
    
    // join매핑을 위한 dto
    private Long deptNo; // 부서 번호
    
    
    public WorkTimeDTO toEntity() {
        return WorkTimeDTO.builder()
        	.wktNo(wktNo)
            .empNo(empNo)
            .wktDate(wktDate)
            .wktStartTime(wktStartTime)
            .wktEndTime(wktEndTime)
            .wktStatus(wktStatus)
            .wktType(wktType)
            .wktRemarks(wktRemarks)
            .build();
    }

    @Builder
	public WorkTimeDTO(Long wktNo, Long empNo, LocalDateTime wktDate, LocalDateTime wktStartTime, LocalDateTime wktEndTime,
			String wktStatus, String wktType, String wktRemarks) {
		this.wktNo = wktNo;
		this.empNo = empNo;
		this.wktDate = wktDate;
		this.wktStartTime = wktStartTime;
		this.wktEndTime = wktEndTime;
		this.wktStatus = wktStatus;
		this.wktType = wktType;
		this.wktRemarks = wktRemarks;
	}
    
    
    
    
    
    
}
