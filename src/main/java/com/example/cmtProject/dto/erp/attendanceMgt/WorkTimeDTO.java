package com.example.cmtProject.dto.erp.attendanceMgt;

import java.time.LocalDate;
import java.time.LocalTime;

import com.example.cmtProject.entity.erp.attendanceMgt.WorkStatus;
import com.example.cmtProject.entity.erp.attendanceMgt.WorkTime;
import com.example.cmtProject.entity.erp.attendanceMgt.WorkType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkTimeDTO {
	
    private Long id; // 근무시간NO
    private Long empNo; // 사원번호
    private LocalDate workDate; // 근무일자
    private LocalTime startTime; // 출근시간
    private LocalTime endTime; // 퇴근시간
    private WorkStatus workStatus; // 근무상태
    private WorkType workType; // 기준근무유형
    private String remarks; // 비고
    
    
    public static WorkTimeDTO fromEntity(WorkTime workTime) {
        return WorkTimeDTO.builder()
            .id(workTime.getWktNo())
            .empNo(workTime.getEmpNo().getEmpNo())
            .workDate(workTime.getWorkDate())
            .startTime(workTime.getStartTime())
            .endTime(workTime.getEndTime())
            .workStatus(workTime.getWorkStatus())
            .workType(workTime.getWorkType())
            .remarks(workTime.getRemarks())
            .build();
    }
    
    
    
    
}
