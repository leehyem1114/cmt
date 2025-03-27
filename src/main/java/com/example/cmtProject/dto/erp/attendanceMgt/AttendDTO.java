package com.example.cmtProject.dto.erp.attendanceMgt;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.cmtProject.dto.comm.CommonCodeDetailDTO;
import com.example.cmtProject.entity.erp.attendanceMgt.Attend;
import com.example.cmtProject.entity.erp.attendanceMgt.AttendStatus;
import com.example.cmtProject.entity.erp.attendanceMgt.AttendType;
import com.example.cmtProject.entity.erp.employees.Employees;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
public class AttendDTO {
	@JsonProperty("atdno")
    private Long atdNo; // 출결NO
    @JsonProperty("empno")
    private Long empNo; // 사원번호
    @JsonProperty("empname")
    private String empName; // 사원이름
    @JsonProperty("attenddate")
    private LocalDateTime attendDate; // 출결일자
    @JsonProperty("attendtype")
    private String attendType; // 출결유형
    @JsonProperty("attendstatus")
    private String attendStatus; // 출결상태
    private String remarks; // 비고
    
    public Attend toEntity() {
        return Attend.builder()
            .empNo(empNo)
            .empName(empName)
            .attendDate(attendDate)
            .attendType(attendType)
            .attendStatus(attendStatus)
            .remarks(remarks)
            .build();
    }

    @Builder
	public AttendDTO(Long atdNo, Long empNo, String empName, LocalDateTime attendDate, String attendType, String attendStatus,
			String remarks) {
		this.atdNo = atdNo;
		this.empNo = empNo;
		this.empName = empName;
		this.attendDate = attendDate;
		this.attendType = attendType;
		this.attendStatus = attendStatus;
		this.remarks = remarks;
	}
    


    
    
}
