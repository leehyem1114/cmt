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
@Builder
public class AttendDTO {
    private Long id; // 출결NO
    private Employees empNo; // 사원번호
    private LocalDate attendDate; // 출결일자
    private AttendType attendType; // 출결유형
    private AttendStatus attendStatus; // 출결상태
    private String remarks; // 비고
    
    public Attend toEntity() {
        return Attend.builder()
            .empNo(empNo)
            .attendDate(attendDate)
            .attendType(attendType)
            .attendStatus(attendStatus)
            .remarks(remarks)
            .build();
    }

    @Builder
	public AttendDTO(Long id, Employees empNo, LocalDate attendDate, AttendType attendType, AttendStatus attendStatus,
			String remarks) {
		this.id = id;
		this.empNo = empNo;
		this.attendDate = attendDate;
		this.attendType = attendType;
		this.attendStatus = attendStatus;
		this.remarks = remarks;
	}
    
    


    
    
}
