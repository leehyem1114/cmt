package com.example.cmtProject.dto.erp.attendanceMgt;

import java.time.LocalDate;

import com.example.cmtProject.entity.erp.attendanceMgt.AttendStatus;
import com.example.cmtProject.entity.erp.attendanceMgt.AttendType;

import groovy.transform.builder.Builder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendDTO {
    private Long id; // 출결NO
    private Long employeeId; // 사원번호
    private LocalDate attendDate; // 출결일자
    private AttendType attendType; // 출결유형
    private AttendStatus attendStatus; // 출결상태
    private String remarks; // 비고

//    public static Attend toEntity(AttendDTO dto, Employee employee) {
//        return Attend.builder()
//            .employee(employee)
//            .attendDate(dto.getAttendDate())
//            .attendType(dto.getAttendType())
//            .attendStatus(dto.getAttendStatus())
//            .remarks(dto.getRemarks())
//            .build();
//    }


    
    
}
