package com.example.cmtProject.dto.erp.attendanceMgt;

import java.time.LocalDate;

import com.example.cmtProject.entity.erp.attendanceMgt.Attend;
import com.example.cmtProject.entity.erp.attendanceMgt.AttendStatus;
import com.example.cmtProject.entity.erp.attendanceMgt.AttendType;
import com.example.cmtProject.entity.erp.employees.Employees;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendDTO {
    private Long id; // 출결NO
    private Long empNo; // 사원번호
    private LocalDate attendDate; // 출결일자
    private AttendType attendType; // 출결유형
    private AttendStatus attendStatus; // 출결상태
    private String remarks; // 비고
    
    public static AttendDTO fromEntity(Attend attend) {
        return AttendDTO.builder()
            .id(attend.getAtdNo())
            .empNo(attend.getEmpNo().getEmpNo())
            .attendDate(attend.getAttendDate())
            .attendType(attend.getAttendType())
            .attendStatus(attend.getAttendStatus())
            .remarks(attend.getRemarks())
            .build();
    }


    
    
}
