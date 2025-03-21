package com.example.cmtProject.dto.erp.attendanceMgt;

import java.time.LocalDate;
import java.time.LocalTime;

import com.example.cmtProject.entity.erp.attendanceMgt.WorkTime;

import groovy.transform.builder.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkTimeDto {
	
    private Long id; // 근무시간NO
    private Long employeeId; // 사원번호
    private LocalDate workDate; // 근무일자
    private LocalTime startTime; // 출근시간
    private LocalTime endTime; // 퇴근시간
    private WorkStatus workStatus; // 근무상태
    private WorkType workType; // 기준근무유형
    private String remarks; // 비고
    
    
    public static WorkTimeDto fromEntity(WorkTime workTime) {
        return WorkTimeDto.builder()
            .id(workTime.getId())
            .employeeId(workTime.getEmployee().getId())
            .workDate(workTime.getWorkDate())
            .startTime(workTime.getStartTime())
            .endTime(workTime.getEndTime())
            .workStatus(workTime.getWorkStatus())
            .workType(workTime.getWorkType())
            .remarks(workTime.getRemarks())
            .build();
    }

}
