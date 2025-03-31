package com.example.cmtProject.dto.erp.attendanceMgt;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.cmtProject.dto.comm.CommonCodeDetailDTO;
import com.example.cmtProject.entity.erp.attendanceMgt.Attend;
import com.example.cmtProject.entity.erp.attendanceMgt.AttendStatus;
import com.example.cmtProject.entity.erp.attendanceMgt.AttendType;
import com.example.cmtProject.entity.erp.employees.Employees;
import com.fasterxml.jackson.annotation.JsonFormat;
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
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("attenddate")
    private LocalDateTime attendDate; // 출근일자
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("attendleave")
    private LocalDateTime attendLeave; // 퇴근일자
    
    @JsonProperty("attendtype")
    private CommonCodeDetailDTO attendType; // 출결유형
    
    @JsonProperty("attendstatus")
    private CommonCodeDetailDTO attendStatus; // 출결상태
    
    private String remarks; // 비고
    
    public Attend toEntity() {
        return Attend.builder()
            .empNo(empNo)
            .empName(empName)
            .attendDate(attendDate)
            .attendLeave(attendLeave)
            .attendType(attendType)
            .attendStatus(attendStatus)
            .remarks(remarks)
            .build();
    }

    @Builder
	public AttendDTO(Long atdNo, Long empNo, String empName, LocalDateTime attendDate, LocalDateTime attandLeave, 
			CommonCodeDetailDTO attendType, CommonCodeDetailDTO attendStatus, String remarks) {
		this.atdNo = atdNo;
		this.empNo = empNo;
		this.empName = empName;
		this.attendDate = attendDate;
		this.attendLeave = attandLeave;
		this.attendType = attendType;
		this.attendStatus = attendStatus;
		this.remarks = remarks;
	}
    


    
}
















